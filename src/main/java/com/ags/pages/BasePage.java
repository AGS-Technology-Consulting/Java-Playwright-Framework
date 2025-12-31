package com.ags.pages;

import com.ags.config.Config;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePage {
    protected Page page;
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    
    public BasePage(Page page) {
        this.page = page;
    }
    
    protected void navigateTo(String url) {
        page.navigate(url);
        logger.info("Navigated to: {}", url);
    }
    
    protected void click(String selector) {
        page.click(selector);
        logger.info("Clicked: {}", selector);
    }
    
    protected void fill(String selector, String text) {
        page.fill(selector, text);
        logger.info("Filled text into: {}", selector);
    }
    
    protected String getText(String selector) {
        return page.textContent(selector);
    }
    
    protected boolean isVisible(String selector) {
        try {
            return page.isVisible(selector);
        } catch (Exception e) {
            return false;
        }
    }
    
    protected void waitForSelector(String selector) {
        page.waitForSelector(selector);
    }
    
    protected String getCurrentUrl() {
        return page.url();
    }
}
