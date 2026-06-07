package com.learningAssistant.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.learningAssistant.core.*;

public class LLM {

    private static final String TOKEN_USAGE_FILE = "token_usage.txt";
    private static final long MIN_REQUEST_INTERVAL_MS = 2000;
    private static final long MAX_TOKENS_PER_SESSION = 10000;

    private final TokenManager tokenManager;
    private final RateLimiter rateLimiter;
    private final LLMProvider provider;

    public LLM() {
        this(new OpenAIProvider(),
             new FileTokenManager(TOKEN_USAGE_FILE, MAX_TOKENS_PER_SESSION),
             new SimpleRateLimiter(MIN_REQUEST_INTERVAL_MS));
    }

    public LLM(String model) {
        this(new OpenAIProvider(model),
             new FileTokenManager(TOKEN_USAGE_FILE, MAX_TOKENS_PER_SESSION),
             new SimpleRateLimiter(MIN_REQUEST_INTERVAL_MS));
    }

    public LLM(LLMProvider provider, TokenManager tokenManager, RateLimiter rateLimiter) {
        this.provider = provider;
        this.tokenManager = tokenManager;
        this.rateLimiter = rateLimiter;
    }

    public LLM(String model, TokenManager tokenManager, RateLimiter rateLimiter) {
        this(new OpenAIProvider(model), tokenManager, rateLimiter);
    }

    /**
     * Returns the total tokens used in the current session.
     */
    public long getTotalTokensUsed() {
        return tokenManager.getTotalTokensUsed();
    }

    /**
     * Class to represent a prompt for the LLM, including messages and optional tools
     */
    public static class Prompt {
        private List<Message> messages;
        private List<Tool> tools;
        private Map<String, Object> metadata;


        public Prompt(List<Message> messages) {
            this.messages = messages;
            this.tools = new ArrayList<>();
            this.metadata = new HashMap<>();
        }

        public Prompt(List<Message> messages, List<Tool> tools) {
            this.messages = messages;
            this.tools = tools != null ? tools : new ArrayList<>();
            this.metadata = new HashMap<>();
        }

        public Prompt(List<Message> messages, List<Tool> tools, Map<String, Object> metadata) {
            this.messages = messages;
            this.tools = tools != null ? tools : new ArrayList<>();
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }

        public List<Message> getMessages() {
            return messages;
        }

        public List<Tool> getTools() {
            return tools;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }


    /**
     * Generates an LLM response based on the provided prompt.
     *
     * @param prompt A Prompt object containing messages, optional tools, and metadata.
     * @return The generated response as a String.
     */
    public String generateResponse(Prompt prompt) {
        // --- Rate Limiting Check ---
        rateLimiter.checkRateLimit();

        List<Map<String, Object>> toolMaps = prompt.getTools().stream()
                .map(tool -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", tool.getToolName());
                    map.put("description", tool.getDescription());
                    map.put("parameters", tool.getParameters());
                    return map;
                })
                .collect(Collectors.toList());

        LLMProvider.Context context = new LLMProvider.Context(
                prompt.getMessages().stream()
                        .map(m -> new com.learningAssistant.core.Message(m.getRole(), m.getContent()))
                        .collect(Collectors.toList()),
                toolMaps,
                prompt.getMetadata()
        );

        LLMProvider.Result result = provider.generateCompletion(context);

        // Token Usage Tracking
        tokenManager.addTokens(result.getTokensUsed());
        System.out.println("\n--- Token Usage ---");
        System.out.println("Total tokens used in this request: " + result.getTokensUsed());
        System.out.println("Total persistent tokens: " + tokenManager.getTotalTokensUsed());
        System.out.println("-------------------\n");

        return result.getContent();
    }

    /**
     * Convenience method to generate a response from just messages
     */
    public String generateResponse(List<Message> messages) {
        return generateResponse(new Prompt(messages));
    }
}