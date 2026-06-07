package com.learningAssistant.analysis;

import com.learningAssistant.core.FileTokenManager;
import com.learningAssistant.core.LLMProvider;
import com.learningAssistant.core.OpenAIProvider;
import com.learningAssistant.core.SimpleRateLimiter;
import com.learningAssistant.core.*;
import java.util.*;

public class CurriculumResumeSystem {

    public static void main(String[] args) throws Exception {
        LLMProvider provider;
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("Using MockAnalysisProvider because OPENAI_API_KEY is not set.");
            provider = new MockAnalysisProvider();
        } else {
            provider = new OpenAIProvider();
        }

        LLM llm = new LLM(provider, 
                          new FileTokenManager("token_usage.txt", 10000), 
                          new SimpleRateLimiter(0));

        // 1. Define Goals for the planning agent
        List<Goal> plannerGoals = Arrays.asList(
            new Goal(1, "Analyze curriculum", "Evaluate the curriculum for key learning outcomes."),
            new Goal(2, "Analyze resume", "Evaluate the resume for relevant skills and experience."),
            new Goal(3, "Generate SOP", "Use both analyses to generate a Statement of Purpose."),
            new Goal(4, "Generate Study Plan", "Use both analyses to generate a Study Plan."),
            new Goal(5, "Complete Task", "Provide the final results and call finishTask.")
        );

        // 2. Create the Agent using our tools
        // We'll use the JsonActionLanguage for clear structured interaction
        Agent plannerAgent = Agents.createJsonAgent(AnalysisTools.class, plannerGoals, llm);

        // 3. Define the input data
        // For demonstration, we'll provide file paths for curriculum and resume PDFs
        String curriculumPdf = "curriculum.pdf";
        String resumePdf = "resume.pdf";

        String userInput = "I have a curriculum in '" + curriculumPdf + "' and a resume in '" + resumePdf + "'. " +
                           "Please read these PDF files, analyze them, and then " +
                           "prepare a Statement of Purpose and a Study Plan.";

        // 4. Run the Agent
        System.out.println("Starting Multi-Agent System demonstration...");
        Memory finalMemory = plannerAgent.run(userInput, new Memory(), 10);

        System.out.println("\n--- Final Agent Memory ---");
        finalMemory.getMemories().forEach(m -> {
            System.out.println(m.get("type") + ": " + m.get("content"));
        });
    }
}
