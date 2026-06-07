package com.learningAssistant.core;

import java.util.List;

public interface AgentLanguage {
    LLM.Prompt constructPrompt(List<Tool> tools, Environment environment, List<Goal> goals, Memory memory);
    java.util.Map<String, Object> parseResponse(String response);
    String getActionResponse(ActionResult result);
    String getGoalCompletionPrompt();
}
