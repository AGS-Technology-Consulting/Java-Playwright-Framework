package com.ags.pages;

import com.ags.config.Config;
import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {
    private static final String USERNAME_INPUT = "#username";
    private static final String PASSWORD_INPUT = "#password";
    private static final String LOGIN_BUTTON = "button[type='submit']";
    private static final String FLASH_MESSAGE = "#flash";
    private static final String LOGOUT_BUTTON = "a[href='/logout']";
    private static final String PAGE_HEADER = "h2";
    
    public LoginPage(Page page) {
        super(page);
    }
    
    public void open() {
        navigateTo(Config.BASE_URL + "/login");
        waitForSelector(USERNAME_INPUT);
    }
    
    public void enterUsername(String username) {
        fill(USERNAME_INPUT, username);
    }
    
    public void enterPassword(String password) {
        fill(PASSWORD_INPUT, password);
    }
    
    public void clickLoginButton() {
        click(LOGIN_BUTTON);
    }
    
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        page.waitForTimeout(1000);
    }
    
    public String getFlashMessage() {
        waitForSelector(FLASH_MESSAGE);
        return getText(FLASH_MESSAGE).replace("×", "").trim();
    }
    
    public boolean isLoginSuccessful() {
        return getCurrentUrl().contains("/secure");
    }
    
    public boolean isStillOnLoginPage() {
        return getCurrentUrl().contains("/login");
    }
    
    public boolean isLogoutButtonVisible() {
        return isVisible(LOGOUT_BUTTON);
    }
    
    public void logout() {
        click(LOGOUT_BUTTON);
        page.waitForTimeout(1000);
    }
    
    public boolean areLoginPageElementsVisible() {
        return isVisible(USERNAME_INPUT) && isVisible(PASSWORD_INPUT) && isVisible(LOGIN_BUTTON);
    }
    
    public String getPageHeaderText() {
        return getText(PAGE_HEADER);
    }
}
