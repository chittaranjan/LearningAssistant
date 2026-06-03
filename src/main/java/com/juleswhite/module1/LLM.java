package com.juleswhite.module1;

import java.util.List;

public class LLM {

    private static final String TOKEN_USAGE_FILE = "token_usage.txt";
    private static final long MIN_REQUEST_INTERVAL_MS = 2000;
    private static final long MAX_TOKENS_PER_SESSION = 10000;

    private final TokenManager tokenManager;
    private final RateLimiter rateLimiter;
    private final LLMProvider provider;

    public LLM() {
        this(new FileTokenManager(TOKEN_USAGE_FILE, MAX_TOKENS_PER_SESSION),
             new SimpleRateLimiter(MIN_REQUEST_INTERVAL_MS),
             new OpenAIProvider());
    }

    public LLM(TokenManager tokenManager, RateLimiter rateLimiter) {
        this(tokenManager, rateLimiter, new OpenAIProvider());
    }

    public LLM(TokenManager tokenManager, RateLimiter rateLimiter, LLMProvider provider) {
        this.tokenManager = tokenManager;
        this.rateLimiter = rateLimiter;
        this.provider = provider;
    }

    /**
     * Returns the total tokens used in the current session.
     */
    public long getTotalTokensUsed() {
        return tokenManager.getTotalTokensUsed();
    }

    /**
     * Generates an LLM response based on the provided messages.
     *
     * @param messages List of Message objects containing role and content.
     * @return The generated response as a String.
     */
    public String generateResponse(List<Message> messages) {
        // --- Rate Limiting Check ---
        rateLimiter.checkRateLimit();

        LLMProvider.Result result = provider.generateCompletion(new LLMProvider.Context(messages));

        // Token Usage Tracking
        tokenManager.addTokens(result.getTokensUsed());
        System.out.println("\n--- Token Usage ---");
        System.out.println("Total tokens used in this request: " + result.getTokensUsed());
        System.out.println("Total persistent tokens: " + tokenManager.getTotalTokensUsed());
        System.out.println("-------------------\n");

        return result.getContent();
    }
}
