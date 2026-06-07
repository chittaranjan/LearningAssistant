package com.learningAssistant.core.service;

import com.learningAssistant.analysis.AnalysisTools;
import com.learningAssistant.analysis.MockAnalysisProvider;
import com.learningAssistant.core.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AgenticService that uses the local Java GAME framework.
 */
@Service
public class LocalJavaAgenticService implements AgenticService {

    @Override
    public Map<String, Object> analyze(Map<String, Object> context) throws Exception {
        return analyzeInternal(context, null);
    }

    @Override
    public void analyze(Map<String, Object> context, ProgressCallback callback) throws Exception {
        analyzeInternal(context, callback);
    }

    private Map<String, Object> analyzeInternal(Map<String, Object> context, ProgressCallback callback) throws Exception {
        String curriculumText = (String) context.get("curriculumText");
        String resumeText = (String) context.get("resumeText");

        LLMProvider provider;
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            provider = new MockAnalysisProvider(curriculumText, resumeText);
        } else {
            provider = new OpenAIProvider();
        }

        LLM llm = new LLM(provider, 
                          new FileTokenManager("token_usage.txt", 100000), 
                          new SimpleRateLimiter(0));

        List<Goal> plannerGoals = Arrays.asList(
            new Goal(1, "Analyze curriculum", "Evaluate the curriculum for key learning outcomes."),
            new Goal(2, "Analyze resume", "Evaluate the resume for relevant skills and experience."),
            new Goal(3, "Generate SOP", "Use both analyses to generate a Statement of Purpose."),
            new Goal(4, "Generate Study Plan", "Use both analyses to generate a Study Plan."),
            new Goal(5, "Complete Task", "Provide the final results and call finishTask.")
        );

        String customPrompt = (String) context.get("customPrompt");
        Agent plannerAgent = Agents.createInstanceAgent(
            new AnalysisTools(llm, customPrompt), 
            plannerGoals, 
            llm
        );

        String userInput = "Analyze this curriculum text: [" + curriculumText + "] and this resume text: [" + resumeText + "]. ";
        
        if (customPrompt != null && !customPrompt.isEmpty()) {
            userInput += "Additional instructions: " + customPrompt + " ";
        }
        
        userInput += "Prepare a Statement of Purpose and a Study Plan.";

        Memory finalMemory = plannerAgent.run(userInput, new Memory(), 10, callback);

        Map<String, Object> response = new HashMap<>();
        response.put("memory", finalMemory.getMemories());
        
        List<String> results = finalMemory.getMemories().stream()
                .filter(m -> "assistant".equals(m.get("type")))
                .map(m -> (String) m.get("content"))
                .collect(Collectors.toList());
        response.put("results", results);

        return response;
    }
}
