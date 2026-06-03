package com.juleswhite.module1;

import java.util.List;
import java.util.Map;

public interface LLMProvider {
    /**
     * Context class to hold parameters for a completion request.
     * Includes messages and optional tools/metadata.
     */
    class Context {
        private final List<Message> messages;
        private final List<Map<String, Object>> tools; // Simplified tool representation
        private final Map<String, Object> metadata;

        public Context(List<Message> messages) {
            this(messages, null, null);
        }

        public Context(List<Message> messages, List<Map<String, Object>> tools, Map<String, Object> metadata) {
            this.messages = messages;
            this.tools = tools;
            this.metadata = metadata;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public List<Map<String, Object>> getTools() {
            return tools;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }

    /**
     * Result class to hold the LLM's response and token usage.
     */
    class Result {
        private final String content;
        private final long tokensUsed;

        public Result(String content, long tokensUsed) {
            this.content = content;
            this.tokensUsed = tokensUsed;
        }

        public String getContent() {
            return content;
        }

        public long getTokensUsed() {
            return tokensUsed;
        }
    }

    Result generateCompletion(Context context);
}
