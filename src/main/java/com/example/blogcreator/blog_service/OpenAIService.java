package com.example.blogcreator.blog_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class OpenAIService {

    // Inject the OpenAI API key from application.properties
    @Value("${openai.api.key}")
    private String openaiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";  // Updated endpoint

    // Method to generate the summary from OpenAI
    public String generateSummary(String blogContent) {
        if (blogContent == null || blogContent.trim().isEmpty()) {
            return "Blog content is empty. Please provide valid content.";
        }
    
        try {
            // Prepare the request body (in JSON format)
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");  // Use gpt-3.5-turbo
    
            // Prepare the messages array for the chat model
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");  // The role of the message
            userMessage.put("content", "Summarize the following blog post:\n" + blogContent);  // The actual prompt
            messages.put(userMessage);
    
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 150);  // Limit the summary to 150 tokens
    
            // Prepare the HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openaiApiKey);  // Using the injected API key
            headers.setContentType(MediaType.APPLICATION_JSON);
    
            // Prepare the HTTP entity with headers and body
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);
    
            // Initialize RestTemplate
            RestTemplate restTemplate = new RestTemplate();
    
            // Retry logic for handling 429 errors (rate limit exceeded)
            int attempts = 0;
            final int maxAttempts = 3;  // Retry up to 3 times
            long retryInterval = 1000;  // Retry every 1 second (adjust as needed)
    
            while (attempts < maxAttempts) {
                try {
                    ResponseEntity<String> response = restTemplate.exchange(
                            OPENAI_API_URL, HttpMethod.POST, requestEntity, String.class);
    
                    // If the request is successful, parse the response
                    if (response.getStatusCode() == HttpStatus.OK) {
                        String responseBody = response.getBody();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String summary = jsonResponse.getJSONArray("choices")
                                                     .getJSONObject(0)
                                                     .getJSONObject("message")
                                                     .getString("content");
    
                        return summary.trim();  // Return the summarized text
                    } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        // If rate-limited, retry after some time (extracted from Retry-After header if available)
                        String retryAfter = response.getHeaders().getFirst("Retry-After");
                        if (retryAfter != null) {
                            retryInterval = Long.parseLong(retryAfter) * 1000;  // Retry after the specified time in seconds
                        }
                        attempts++;
                        Thread.sleep(retryInterval);  // Wait before retrying
                    } else {
                        return "Error: " + response.getStatusCode() + " - " + response.getBody();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // Restore interrupted status
                    return "Error generating summary: " + e.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error generating summary: " + e.getMessage();
                }
            }
    
            return "Error: Exceeded maximum retry attempts.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating summary: " + e.getMessage();  // Handle API errors
        }
    }
    
}
