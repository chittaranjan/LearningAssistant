package com.learningAssistant.core.service;

import java.util.Map;

/**
 * Placeholder for a remote agentic service (e.g., Python/LangGraph).
 * This demonstrates how easy it would be to swap the implementation.
 */
public class RemoteAgenticService implements AgenticService {

    private final String remoteUrl;

    public RemoteAgenticService(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    @Override
    public Map<String, Object> analyze(Map<String, Object> context) throws Exception {
        // In a real implementation, this would make a REST call to the remote service.
        // Example (pseudo-code):
        // return restTemplate.postForObject(remoteUrl + "/analyze", context, Map.class);
        throw new UnsupportedOperationException("RemoteAgenticService is not yet implemented.");
    }

    @Override
    public void analyze(Map<String, Object> context, com.learningAssistant.core.ProgressCallback callback) throws Exception {
        throw new UnsupportedOperationException("RemoteAgenticService is not yet implemented.");
    }
}
