package com.juleswhite.module1;

public class SimpleRateLimiter implements RateLimiter {
    private final long minRequestIntervalMs;
    private long lastRequestTime = 0;

    public SimpleRateLimiter(long minRequestIntervalMs) {
        this.minRequestIntervalMs = minRequestIntervalMs;
    }

    @Override
    public void checkRateLimit() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime < minRequestIntervalMs) {
            throw new RuntimeException("Rate limit exceeded: Please wait at least " + (minRequestIntervalMs / 1000) + " seconds between requests.");
        }
        lastRequestTime = currentTime;
    }
}
