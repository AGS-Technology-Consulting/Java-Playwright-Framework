package com.ags.tests;

import com.ags.config.Config;
import com.ags.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    private LoginPage loginPage;
    
    @BeforeMethod
    public void setupTest() {
        loginPage = new LoginPage(page);
    }
    
    @Test(priority = 1, groups = {"smoke", "ui-validation"})
    public void testVerifyLoginPageElements() {
        logger.info("🧪 TC-01: Verify Login Page Elements");
        loginPage.open();
        Assert.assertTrue(loginPage.areLoginPageElementsVisible());
        Assert.assertEquals(loginPage.getPageHeaderText(), "Login Page");
        logger.info("✅ TC-01 Passed");
    }
    
    @Test(priority = 2, groups = {"smoke", "positive"})
    public void testLoginWithValidCredentials() {
        logger.info("🧪 TC-02: Login With Valid Credentials");
        loginPage.open();
        loginPage.login(Config.VALID_USERNAME, Config.VALID_PASSWORD);
        Assert.assertTrue(loginPage.isLoginSuccessful());
        Assert.assertTrue(loginPage.isLogoutButtonVisible());
        Assert.assertTrue(loginPage.getFlashMessage().contains("You logged into a secure area"));
        logger.info("✅ TC-02 Passed");
    }
    
    @Test(priority = 3, groups = {"smoke", "negative"})
    public void testLoginWithInvalidUsername() {
        logger.info("🧪 TC-03: Login With Invalid Username");
        loginPage.open();
        loginPage.login(Config.INVALID_USERNAME, Config.VALID_PASSWORD);
        Assert.assertTrue(loginPage.isStillOnLoginPage());
        Assert.assertTrue(loginPage.getFlashMessage().contains("Your username is invalid"));
        logger.info("✅ TC-03 Passed");
    }
    
    @Test(priority = 4, groups = {"regression", "negative"})
    public void testLoginWithInvalidPassword() {
        logger.info("🧪 TC-04: Login With Invalid Password");
        loginPage.open();
        loginPage.login(Config.VALID_USERNAME, Config.INVALID_PASSWORD);
        Assert.assertTrue(loginPage.isStillOnLoginPage());
        Assert.assertTrue(loginPage.getFlashMessage().contains("Your password is invalid"));
        logger.info("✅ TC-04 Passed");
    }
    
    @Test(priority = 5, groups = {"smoke", "validation"})
    public void testLoginWithEmptyCredentials() {
        logger.info("🧪 TC-05: Login With Empty Credentials");
        loginPage.open();
        loginPage.clickLoginButton();
        Assert.assertTrue(loginPage.isStillOnLoginPage());
        Assert.assertTrue(loginPage.getFlashMessage().contains("Your username is invalid"));
        logger.info("✅ TC-05 Passed");
    }
    
    @Test(priority = 6, groups = {"regression", "positive"})
    public void testLogoutFunctionality() {
        logger.info("🧪 TC-06: Logout Functionality");
        loginPage.open();
        loginPage.login(Config.VALID_USERNAME, Config.VALID_PASSWORD);
        Assert.assertTrue(loginPage.isLoginSuccessful());
        loginPage.logout();
        Assert.assertTrue(loginPage.isStillOnLoginPage());
        Assert.assertTrue(loginPage.getFlashMessage().contains("You logged out"));
        logger.info("✅ TC-06 Passed");
    }
}
