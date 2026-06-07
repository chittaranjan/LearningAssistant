package com.learningAssistant.api;

import com.learningAssistant.core.FileTokenManager;
import com.learningAssistant.core.LLMProvider;
import com.learningAssistant.core.OpenAIProvider;
import com.learningAssistant.core.SimpleRateLimiter;
import com.learningAssistant.core.*;
import com.learningAssistant.analysis.AnalysisTools;
import com.learningAssistant.analysis.MockAnalysisProvider;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*")
public class AgentController {

    @PostMapping("/analyze")
    public Map<String, Object> analyze(@RequestParam("curriculum") MultipartFile curriculumFile,
                                       @RequestParam("resume") MultipartFile resumeFile) throws Exception {
        
        String curriculumText = extractText(curriculumFile);
        String resumeText = extractText(resumeFile);

        LLMProvider provider;
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            provider = new MockAnalysisProvider();
        } else {
            provider = new OpenAIProvider();
        }

        LLM llm = new LLM(provider, 
                          new FileTokenManager("token_usage.txt", 10000), 
                          new SimpleRateLimiter(0));

        List<Goal> plannerGoals = Arrays.asList(
            new Goal(1, "Analyze curriculum", "Evaluate the curriculum for key learning outcomes."),
            new Goal(2, "Analyze resume", "Evaluate the resume for relevant skills and experience."),
            new Goal(3, "Generate SOP", "Use both analyses to generate a Statement of Purpose."),
            new Goal(4, "Generate Study Plan", "Use both analyses to generate a Study Plan."),
            new Goal(5, "Complete Task", "Provide the final results and call finishTask.")
        );

        Agent plannerAgent = Agents.createJsonAgent(AnalysisTools.class, plannerGoals, llm);

        String userInput = "Analyze this curriculum text: [" + curriculumText + "] and this resume text: [" + resumeText + "]. " +
                           "Prepare a Statement of Purpose and a Study Plan.";

        Memory finalMemory = plannerAgent.run(userInput, new Memory(), 10);

        Map<String, Object> response = new HashMap<>();
        response.put("memory", finalMemory.getMemories());
        
        // Extract key results for easy display
        List<String> results = finalMemory.getMemories().stream()
                .filter(m -> "assistant".equals(m.get("type")))
                .map(m -> (String) m.get("content"))
                .collect(Collectors.toList());
        response.put("results", results);

        return response;
    }

    private String extractText(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
