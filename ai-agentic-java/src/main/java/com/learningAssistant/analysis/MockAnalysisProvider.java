package com.learningAssistant.analysis;

import com.learningAssistant.core.LLMProvider;
import com.learningAssistant.core.Message;
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
        // If there are no tools provided in the context, this is likely a direct call from AnalysisTools
        // for content generation, rather than the agent planning loop.
        if (context.getTools() == null || context.getTools().isEmpty()) {
            String userMessage = "";
            for (Message m : context.getMessages()) {
                if ("user".equals(m.getRole())) {
                    userMessage = m.getContent();
                    break;
                }
            }

            if (userMessage.contains("evaluateCurriculum")) {
                return new Result("### Mock Curriculum Analysis\n- Key Outcomes: Java, Spring Boot, AI\n- Prerequisites: Computer Science basics", 50);
            } else if (userMessage.contains("evaluateResume")) {
                return new Result("### Mock Resume Analysis\n- Experience: Senior SDE\n- Skills: Java, Kubernetes", 50);
            } else if (userMessage.contains("generateSOP")) {
                return new Result("### Mock Statement of Purpose\nI am applying for this course to further my skills in AI and Spring Boot.", 100);
            } else if (userMessage.contains("generateStudyPlan")) {
                return new Result("### Mock Study Plan\n1. Review AI basics.\n2. Complete Spring Boot project.", 100);
            }
            return new Result("Detailed mock analysis content based on the provided input.", 100);
        }

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
                    args.put("curriculumAnalysis", "### Curriculum Analysis\n- **Focus**: Advanced ML, Neural Networks\n- **Intensity**: High");
                    args.put("resumeAnalysis", "### Resume Analysis\n- **Skills**: Java, Spring Boot\n- **Experience**: Senior SDE");
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
            String formattedResponse = "```action\n" + json + "\n```";
            
            return new Result(formattedResponse, 100);
        } catch (Exception e) {
            return new Result("Error in mock", 0);
        }
    }
}
