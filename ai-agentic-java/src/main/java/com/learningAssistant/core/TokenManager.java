package com.learningAssistant.core;

public interface TokenManager {
    long getTotalTokensUsed();
    void addTokens(long tokens);
}
