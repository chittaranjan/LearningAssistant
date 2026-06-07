package com.learningAssistant.core;

import java.util.Map;

/**
 * Callback interface for receiving progress updates from the agent during execution.
 */
public interface ProgressCallback {
    /**
     * Called when the agent makes a decision (e.g., chooses a tool).
     * @param decision The agent's decision/thought.
     */
    void onDecision(String decision);

    /**
     * Called when an action is completed.
     * @param result The result of the action.
     */
    void onActionResult(Map<String, Object> result);
    
    /**
     * Called when the entire task is complete.
     * @param finalMemory The final memory state.
     */
    void onComplete(Memory finalMemory);

    /**
     * Called if an error occurs.
     * @param error The error message.
     */
    void onError(String error);
}
