package com.learningAssistant.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;

public class AgentLanguages {

    public static class JsonActionLanguage implements AgentLanguage {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public LLM.Prompt constructPrompt(List<Tool> tools, Environment environment, List<Goal> goals, Memory memory) {
            String goalSummary = goals.stream()
                    .map(g -> "- " + g.getDescription())
                    .collect(Collectors.joining("\n"));

            String toolsSummary = tools.stream()
                    .map(t -> "- " + t.getToolName() + ": " + t.getDescription())
                    .collect(Collectors.joining("\n"));

            String systemPrompt = "You are an agent with the following goals:\n" + goalSummary +
                    "\n\nYou have access to the following tools:\n" + toolsSummary +
                    "\n\nRespond ONLY with a JSON object representing the next action to take. " +
                    "Format: {\"tool\": \"toolName\", \"args\": {\"arg1\": \"value1\"}}";

            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", systemPrompt));
            for (Map<String, Object> m : memory.getMemories()) {
                messages.add(new Message((String) m.get("type"), (String) m.get("content")));
            }

            return new LLM.Prompt(messages, tools);
        }

        @Override
        public Map<String, Object> parseResponse(String response) {
            try {
                int start = response.indexOf("{");
                int end = response.lastIndexOf("}");
                if (start >= 0 && end > start) {
                    response = response.substring(start, end + 1);
                }
                return mapper.readValue(response, Map.class);
            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("tool", "error");
                error.put("args", Map.of("message", e.getMessage()));
                return error;
            }
        }

        @Override
        public String getActionResponse(ActionResult result) {
            return "Action result: " + (result.getError() != null ? result.getError() : result.getResult());
        }

        @Override
        public String getGoalCompletionPrompt() {
            return "All goals have been addressed. Please summarize the results and use the finishTask tool.";
        }
    }

    public static class FunctionCallingLanguage implements AgentLanguage {
        @Override
        public LLM.Prompt constructPrompt(List<Tool> tools, Environment environment, List<Goal> goals, Memory memory) {
            String goalSummary = goals.stream()
                    .map(g -> "- " + g.getDescription())
                    .collect(Collectors.joining("\n"));

            String systemPrompt = "You are an agent with the following goals:\n" + goalSummary;

            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", systemPrompt));
            for (Map<String, Object> m : memory.getMemories()) {
                messages.add(new Message((String) m.get("type"), (String) m.get("content")));
            }

            return new LLM.Prompt(messages, tools);
        }

        @Override
        public Map<String, Object> parseResponse(String response) {
            return null; // Handled by LLM provider for function calling
        }

        @Override
        public String getActionResponse(ActionResult result) {
            return String.valueOf(result.getResult());
        }

        @Override
        public String getGoalCompletionPrompt() {
            return "Task completed.";
        }
    }
}
