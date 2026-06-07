package com.learningAssistant.core.service;

import java.util.Map;

/**
 * Interface representing a generic agentic service.
 * This allows the backend to interact with different agent implementations
 * (local Java GAME agents, remote Python/LangGraph agents, etc.)
 */
public interface AgenticService {
    /**
     * Executes an analysis task using the agentic layer.
     *
     * @param context A map containing the necessary data for analysis (e.g., curriculum text, resume text).
     * @return A map containing the results of the analysis (e.g., SOP, Study Plan, full execution memory).
     * @throws Exception if any error occurs during analysis.
     */
    Map<String, Object> analyze(Map<String, Object> context) throws Exception;
}
