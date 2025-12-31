package com.ags.config;

public class Config {
    // Application Configuration
    public static final String BASE_URL;
    
    // Browser Configuration
    public static final String BROWSER;
    public static final boolean HEADLESS;
    public static final int VIEWPORT_WIDTH = 1920;
    public static final int VIEWPORT_HEIGHT = 1080;
    
    // Timeouts (in milliseconds)
    public static final int PAGE_LOAD_TIMEOUT = 60000;
    public static final int ELEMENT_TIMEOUT = 30000;
    
    // Test Data
    public static final String VALID_USERNAME = "tomsmith";
    public static final String VALID_PASSWORD = "SuperSecretPassword!";
    public static final String INVALID_USERNAME = "invaliduser";
    public static final String INVALID_PASSWORD = "wrongpassword";
    
    // API Configuration
    public static final boolean API_ENABLED;
    public static final String API_BASE_URL = "https://unsobering-maribeth-hokey.ngrok-free.dev";
    public static final String API_TOKEN = "D_YIqZ4D0tYVgFTWKEaRVImEpiq3vzZkOB40lKDDSRk";
    public static final String ORG_ID = "374060a8-925c-49aa-8495-8a823949f3e0";
    public static final String CREATED_BY = "c9279b2d-701c-48eb-9122-fbeae465771c";
    
    // Jenkins Configuration
    public static final boolean IS_JENKINS;
    public static final String BUILD_NUMBER;
    public static final String BUILD_URL;
    public static final String JOB_NAME;
    public static final String GIT_BRANCH;
    public static final String GIT_COMMIT;
    
    // Screenshot Configuration
    public static final String SCREENSHOT_DIR = "screenshots";
    public static final boolean SCREENSHOT_ON_FAILURE = true;
    
    // Static initialization block
    static {
        // Initialize BASE_URL
        String url = System.getProperty("baseUrl");
        if (url == null || url.trim().isEmpty()) {
            BASE_URL = "https://the-internet.herokuapp.com";
        } else {
            BASE_URL = url;
        }
        
        // Initialize BROWSER
        String browser = System.getProperty("browser");
        if (browser == null || browser.trim().isEmpty()) {
            BROWSER = "chromium";
        } else {
            BROWSER = browser;
        }
        
        // Initialize HEADLESS
        String headless = System.getProperty("headless");
        if (headless == null || headless.trim().isEmpty()) {
            HEADLESS = true;
        } else {
            HEADLESS = Boolean.parseBoolean(headless);
        }
        
        // Initialize Jenkins flags
        API_ENABLED = System.getenv("JENKINS_URL") != null || System.getenv("BUILD_NUMBER") != null;
        IS_JENKINS = API_ENABLED;
        
        BUILD_NUMBER = getEnvOrDefault("BUILD_NUMBER", "");
        BUILD_URL = getEnvOrDefault("BUILD_URL", "");
        JOB_NAME = getEnvOrDefault("JOB_NAME", "Playwright-Java");
        GIT_BRANCH = getEnvOrDefault("GIT_BRANCH", "main");
        GIT_COMMIT = getEnvOrDefault("GIT_COMMIT", "");
    }
    
    /**
     * Get environment variable with default value
     */
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
}
