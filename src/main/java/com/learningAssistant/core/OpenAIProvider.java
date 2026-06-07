package com.learningAssistant.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.ChatModel;
import com.openai.models.FunctionDefinition;
import com.openai.models.chat.completions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAIProvider implements LLMProvider {
    private final String model;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIProvider() {
        this(ChatModel.GPT_4O.asString());
    }

    public OpenAIProvider(String model) {
        this.model = model;
    }

    @Override
    public Result generateCompletion(Context context) {
        // Debug: Print environment variable status
        String envKey = System.getenv("OPENAI_API_KEY");
        if (envKey == null || envKey.isEmpty()) {
            System.err.println("[DEBUG] OPENAI_API_KEY is NOT found in System.getenv()");
        }

        OpenAIClient client;
        try {
            client = OpenAIOkHttpClient.fromEnv();
        } catch (Exception e) {
            System.err.println("Error: The OPENAI_API_KEY environment variable is not set.");
            throw new RuntimeException("Missing OPENAI_API_KEY", e);
        }

        ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams.builder()
                .model(model)
                .maxTokens(1024);

        for (Message message : context.getMessages()) {
            if (message.getRole().equals("system")) {
                paramsBuilder.addMessage(ChatCompletionSystemMessageParam.builder()
                        .content(message.getContent()).build());
            } else if (message.getRole().equals("user")) {
                paramsBuilder.addMessage(ChatCompletionUserMessageParam.builder()
                        .content(message.getContent()).build());
            } else {
                paramsBuilder.addMessage(ChatCompletionAssistantMessageParam.builder()
                        .content(message.getContent()).build());
            }
        }

        if (context.getTools() != null && !context.getTools().isEmpty()) {
            List<ChatCompletionTool> tools = new ArrayList<>();
            for (Map<String, Object> toolMap : context.getTools()) {
                tools.add(ChatCompletionTool.builder()
                        .type(JsonValue.from("function"))
                        .function(FunctionDefinition.builder()
                                .name((String) toolMap.get("name"))
                                .description((String) toolMap.get("description"))
                                .parameters(JsonValue.from(toolMap.get("parameters")))
                                .build())
                        .build());
            }
            paramsBuilder.tools(tools);
        }

        ChatCompletion completion = client.chat().completions().create(paramsBuilder.build());

        long tokens = completion.usage().map(usage -> usage.totalTokens()).orElse(0L);
        String content;

        if (completion.choices().get(0).message().toolCalls().isPresent() &&
            !completion.choices().get(0).message().toolCalls().get().isEmpty()) {
            
            ChatCompletionMessageToolCall toolCall = completion.choices().get(0).message().toolCalls().get().get(0);
            try {
                Map<String, Object> toolResponse = new HashMap<>();
                toolResponse.put("tool", toolCall.function().name());
                toolResponse.put("args", objectMapper.readValue(toolCall.function().arguments(), Map.class));
                content = objectMapper.writeValueAsString(toolResponse);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse tool call", e);
            }
        } else {
            content = completion.choices().get(0).message().content().orElse("");
        }

        return new Result(content, tokens);
    }
}
