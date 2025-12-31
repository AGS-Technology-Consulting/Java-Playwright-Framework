package com.ags.tests;

import com.ags.config.Config;
import com.ags.helpers.APIHelper;
import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;
import java.nio.file.Paths;

public class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    @BeforeSuite
    public void beforeSuite() {
        logger.info("=".repeat(63));
        logger.info("🎯 Playwright Java Framework Starting");
        logger.info("=".repeat(63));
        logger.info("💻 Browser: {}", Config.BROWSER);
        logger.info("🎭 Headless: {}", Config.HEADLESS);
        logger.info("=".repeat(63));
        
        playwright = Playwright.create();
        
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(Config.HEADLESS);
        
        browser = switch (Config.BROWSER.toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "webkit" -> playwright.webkit().launch(launchOptions);
            default -> playwright.chromium().launch(launchOptions);
        };
        
        logger.info("✅ Browser initialized");
        APIHelper.beforeAllTests();
    }
    
    @BeforeMethod
    public void beforeMethod() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(Config.VIEWPORT_WIDTH, Config.VIEWPORT_HEIGHT));
        page = context.newPage();
        APIHelper.markTestStart();
    }
    
    @AfterMethod
    public void afterMethod(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String status = result.isSuccess() ? "PASSED" : "FAILED";
        String errorMessage = result.isSuccess() ? null : result.getThrowable().getMessage();
        
        if (!result.isSuccess() && Config.SCREENSHOT_ON_FAILURE) {
            String fileName = System.currentTimeMillis() + "-" + testName + ".png";
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(Config.SCREENSHOT_DIR + "/" + fileName)));
            logger.info("📸 Screenshot saved");
        }
        
        APIHelper.afterEachTest(testName, status, errorMessage);
        
        if (page != null) page.close();
        if (context != null) context.close();
    }
    
    @AfterSuite
    public void afterSuite() {
        logger.info("=".repeat(63));
        logger.info("🏁 Tests Completed");
        logger.info("=".repeat(63));
        
        APIHelper.afterAllTests();
        
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
