package com.learningAssistant.analysis;

import com.learningAssistant.core.LLMProvider;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockAnalysisProvider implements LLMProvider {
    private int step = 0;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String curriculumText;
    private final String resumeText;

    public MockAnalysisProvider() {
        this("", "");
    }

    public MockAnalysisProvider(String curriculumText, String resumeText) {
        this.curriculumText = curriculumText;
        this.resumeText = resumeText;
    }

    @Override
    public Result generateCompletion(Context context) {
        try {
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> args = new HashMap<>();
            
            switch (step) {
                case 0:
                    response.put("tool", "evaluateCurriculum");
                    args.put("curriculumText", curriculumText.isEmpty() ? "Extracted content from curriculum..." : curriculumText);
                    response.put("args", args);
                    step++;
                    break;
                case 1:
                    response.put("tool", "evaluateResume");
                    args.put("resumeText", resumeText.isEmpty() ? "Extracted content from resume..." : resumeText);
                    response.put("args", args);
                    step++;
                    break;
                case 2:
                    response.put("tool", "generateSOP");
                    args.put("curriculumAnalysis", "Advanced ML, Neural Networks");
                    args.put("resumeAnalysis", "Java, Spring Boot");
                    response.put("args", args);
                    step++;
                    break;
                case 3:
                    response.put("tool", "generateStudyPlan");
                    args.put("curriculumAnalysis", "Advanced ML, Neural Networks");
                    args.put("resumeAnalysis", "Java, Spring Boot");
                    response.put("args", args);
                    step++;
                    break;
                default:
                    response.put("tool", "finishTask");
                    args.put("finalOutput", "Successfully analyzed curriculum and resume, generated SOP and Study Plan.");
                    response.put("args", args);
                    break;
            }
            
            String json = mapper.writeValueAsString(response);
            String formattedResponse = "Thinking...\n\n```action\n" + json + "\n```";
            
            return new Result(formattedResponse, 100);
        } catch (Exception e) {
            return new Result("Error in mock", 0);
        }
    }
}
