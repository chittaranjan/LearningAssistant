package com.juleswhite.module1;

public class MockProvider implements LLMProvider {
    @Override
    public Result generateCompletion(Context context) {
        System.out.println("[MockProvider] Received request with " + context.getMessages().size() + " messages.");
        return new Result("This is a mock response from MockProvider.", 42);
    }
}
