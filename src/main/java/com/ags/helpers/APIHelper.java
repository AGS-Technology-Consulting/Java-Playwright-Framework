package com.ags.helpers;

import com.ags.config.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API Helper for Jenkins Integration
 * Tracks test execution and sends data to backend API
 */
public class APIHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(APIHelper.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final boolean isJenkins = Config.IS_JENKINS;
    
    private static String pipelineRunId = null;
    private static final List<Map<String, Object>> testResults = new ArrayList<>();
    private static long testStartTime;
    private static long suiteStartTime;
    
    /**
     * API-1: Create Pipeline Run (Before All Tests)
     */
    public static void beforeAllTests() {
        if (!isJenkins) {
            logger.info("⚠️  Local run detected - Skipping API calls");
            return;
        }
        
        suiteStartTime = System.currentTimeMillis();
        
        try {
            logger.info("\n" + "=".repeat(63));
            logger.info("📡 API-1: Creating Pipeline Run");
            logger.info("=".repeat(63));
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", Config.JOB_NAME + " - Build #" + Config.BUILD_NUMBER);
            payload.put("repo_name", "Playwright-Java-Web");
            payload.put("environment", "qa");
            payload.put("org", Config.ORG_ID);
            payload.put("created_by", Config.CREATED_BY);
            payload.put("build_number", Integer.parseInt(Config.BUILD_NUMBER.isEmpty() ? "0" : Config.BUILD_NUMBER));
            payload.put("build_url", Config.BUILD_URL);
            payload.put("git_branch", Config.GIT_BRANCH);
            payload.put("git_commit", Config.GIT_COMMIT);
            payload.put("status", "running");
            payload.put("started_at", Instant.now().toString());
            
            String response = postRequest("/api/pipeline-runs/", payload);
            
            if (response != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
                pipelineRunId = responseMap.get("id").toString();
                logger.info("✅ API-1 SUCCESS: Pipeline Run Created");
                logger.info("🆔 Pipeline Run ID: {}", pipelineRunId);
            }
            
            logger.info("=".repeat(63) + "\n");
        } catch (Exception e) {
            logger.error("❌ API-1 ERROR: {}", e.getMessage());
        }
    }
    
    /**
     * Mark test start time
     */
    public static void markTestStart() {
        testStartTime = System.currentTimeMillis();
    }
    
    /**
     * API-3: Create Test Case (After Each Test)
     */
    public static void afterEachTest(String testName, String status, String errorMessage) {
        long duration = System.currentTimeMillis() - testStartTime;
        
        Map<String, Object> testResult = new HashMap<>();
        testResult.put("name", testName);
        testResult.put("status", status);
        testResult.put("errorMessage", errorMessage);
        testResult.put("duration", duration);
        
        testResults.add(testResult);
        
        if (!isJenkins || pipelineRunId == null) {
            logger.info("Test completed: {} - {} ({}ms)", testName, status, duration);
            return;
        }
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("pipeline_run", pipelineRunId);
            payload.put("name", testName);
            payload.put("status", status.toLowerCase());
            payload.put("error_message", errorMessage);
            payload.put("duration", duration);
            payload.put("started_at", Instant.ofEpochMilli(testStartTime).toString());
            payload.put("completed_at", Instant.now().toString());
            
            postRequest("/api/test-cases/", payload);
            logger.info("✅ API-3: Test case created - {} ({}) - {}ms", testName, status, duration);
        } catch (Exception e) {
            logger.error("❌ API-3 ERROR: {}", e.getMessage());
        }
    }
    
    /**
     * API-4: Update Pipeline Run (After All Tests)
     */
    public static void afterAllTests() {
        if (!isJenkins || pipelineRunId == null) {
            logger.info("All tests completed");
            return;
        }
        
        try {
            logger.info("\n" + "=".repeat(63));
            logger.info("📡 API-4: Updating Pipeline Run");
            logger.info("=".repeat(63));
            
            long totalDuration = System.currentTimeMillis() - suiteStartTime;
            long passedCount = testResults.stream().filter(t -> "PASSED".equals(t.get("status"))).count();
            long failedCount = testResults.stream().filter(t -> "FAILED".equals(t.get("status"))).count();
            int totalCount = testResults.size();
            
            String overallStatus = failedCount > 0 ? "failed" : "passed";
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("status", overallStatus);
            payload.put("completed_at", Instant.now().toString());
            payload.put("total_tests", totalCount);
            payload.put("passed_tests", passedCount);
            payload.put("failed_tests", failedCount);
            payload.put("duration", totalDuration);
            
            patchRequest("/api/pipeline-runs/" + pipelineRunId + "/", payload);
            
            logger.info("✅ API-4 SUCCESS: Pipeline Run Updated");
            logger.info("📊 Total: {} | ✅ {} | ❌ {}", totalCount, passedCount, failedCount);
            logger.info("⏱️  Duration: {}ms", totalDuration);
            logger.info("=".repeat(63) + "\n");
        } catch (Exception e) {
            logger.error("❌ API-4 ERROR: {}", e.getMessage());
        }
    }
    
    /**
     * POST request
     */
    private static String postRequest(String endpoint, Map<String, Object> payload) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(Config.API_BASE_URL + endpoint);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + Config.API_TOKEN);
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            post.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
            
            return client.execute(post, response -> {
                if (response.getCode() >= 200 && response.getCode() < 300) {
                    return new String(response.getEntity().getContent().readAllBytes());
                }
                return null;
            });
        }
    }
    
    /**
     * PATCH request
     */
    private static void patchRequest(String endpoint, Map<String, Object> payload) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPatch patch = new HttpPatch(Config.API_BASE_URL + endpoint);
            patch.setHeader("Content-Type", "application/json");
            patch.setHeader("Authorization", "Bearer " + Config.API_TOKEN);
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            patch.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
            
            client.execute(patch);
        }
    }
}
