package com.learningAssistant.analysis;

import com.learningAssistant.core.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AnalysisTools {

    private final LLM llm;
    private final String customPrompt;

    public AnalysisTools(LLM llm, String customPrompt) {
        this.llm = llm;
        this.customPrompt = customPrompt;
    }

    @RegisterTool(name = "readFileContent", description = "Reads text content from a PDF, Word (.docx), or Image file given its path.")
    public String readFileContent(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return "Error: File not found at " + filePath;
        }

        String filename = file.getName().toLowerCase();
        try {
            if (filename.endsWith(".pdf")) {
                try (PDDocument document = Loader.loadPDF(file)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                }
            } else if (filename.endsWith(".docx")) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
                     org.apache.poi.xwpf.usermodel.XWPFDocument doc = new org.apache.poi.xwpf.usermodel.XWPFDocument(fis);
                     org.apache.poi.xwpf.extractor.XWPFWordExtractor extractor = new org.apache.poi.xwpf.extractor.XWPFWordExtractor(doc)) {
                    return extractor.getText();
                }
            } else if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                net.sourceforge.tess4j.Tesseract tesseract = new net.sourceforge.tess4j.Tesseract();
                return tesseract.doOCR(file);
            } else {
                return java.nio.file.Files.readString(file.toPath());
            }
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @RegisterTool(name = "evaluateCurriculum", description = "Analyzes a course curriculum and returns key learning outcomes and prerequisites.")
    public String evaluateCurriculum(String curriculumText) {
        if (curriculumText == null || curriculumText.trim().isEmpty() || curriculumText.contains("[OCR Skipping]")) {
            if (curriculumText != null && curriculumText.contains("[OCR Skipping]") && curriculumText.trim().length() < 150) {
                 throw new RuntimeException("Curriculum text could not be extracted (OCR skipped). Please provide a PDF or Word version of the curriculum.");
            }
            if (curriculumText == null || curriculumText.trim().isEmpty()) {
                throw new RuntimeException("Curriculum text is empty. Did you forget to read the curriculum files?");
            }
        }
        
        String prompt = "Analyze the following course curriculum and return key learning outcomes, prerequisites, and intensity level. " +
                "Format the output using Markdown.\n\n" +
                "Curriculum Text:\n" + curriculumText;
        
        if (customPrompt != null && !customPrompt.isEmpty()) {
            prompt += "\n\nAdditional instructions from user: " + customPrompt;
        }

        return llm.generateResponse(new com.learningAssistant.core.LLM.Prompt(
                List.of(new com.learningAssistant.core.Message("user", prompt)),
                List.of()
        ));
    }

    @RegisterTool(name = "evaluateResume", description = "Analyzes a resume and returns candidate skills and experience.")
    public String evaluateResume(String resumeText) {
        if (resumeText == null || resumeText.trim().isEmpty() || resumeText.contains("[OCR Skipping]")) {
            if (resumeText != null && resumeText.contains("[OCR Skipping]") && resumeText.trim().length() < 150) {
                throw new RuntimeException("Resume text could not be extracted (OCR skipped). Please provide a PDF or Word version of the resume.");
            }
            if (resumeText == null || resumeText.trim().isEmpty()) {
                throw new RuntimeException("Resume text is empty. Did you forget to read the resume files?");
            }
        }
        
        String prompt = "Analyze the following resume and return core skills, experience summary, and identified gaps relative to typical advanced tech courses. " +
                "Format the output using Markdown.\n\n" +
                "Resume Text:\n" + resumeText;
        
        if (customPrompt != null && !customPrompt.isEmpty()) {
            prompt += "\n\nAdditional instructions from user: " + customPrompt;
        }

        return llm.generateResponse(new com.learningAssistant.core.LLM.Prompt(
                List.of(new com.learningAssistant.core.Message("user", prompt)),
                List.of()
        ));
    }

    @RegisterTool(name = "generateSOP", description = "Generates a Statement of Purpose based on curriculum analysis and resume analysis.")
    public String generateSOP(String curriculumAnalysis, String resumeAnalysis) {
        String prompt = "Generate a professional Statement of Purpose based on the following curriculum analysis and resume analysis. " +
                "The SOP should be tailored to the course and highlight how the candidate's background makes them a good fit. " +
                "Format the output using Markdown.\n\n" +
                "Curriculum Analysis:\n" + curriculumAnalysis + "\n\n" +
                "Resume Analysis:\n" + resumeAnalysis;
        
        if (customPrompt != null && !customPrompt.isEmpty()) {
            prompt += "\n\nAdditional instructions from user: " + customPrompt;
        }

        return llm.generateResponse(new com.learningAssistant.core.LLM.Prompt(
                List.of(new com.learningAssistant.core.Message("user", prompt)),
                List.of()
        ));
    }

    @RegisterTool(name = "generateStudyPlan", description = "Generates a study plan to prepare for the course based on curriculum and resume analysis.")
    public String generateStudyPlan(String curriculumAnalysis, String resumeAnalysis) {
        String prompt = "Generate a detailed Study Plan to prepare for the course based on the following curriculum analysis and resume analysis. " +
                "Focus on bridging the identified gaps. Format the output using Markdown.\n\n" +
                "Curriculum Analysis:\n" + curriculumAnalysis + "\n\n" +
                "Resume Analysis:\n" + resumeAnalysis;
        
        if (customPrompt != null && !customPrompt.isEmpty()) {
            prompt += "\n\nAdditional instructions from user: " + customPrompt;
        }

        return llm.generateResponse(new com.learningAssistant.core.LLM.Prompt(
                List.of(new com.learningAssistant.core.Message("user", prompt)),
                List.of()
        ));
    }
    
    @RegisterTool(name = "finishTask", description = "Call this when the task is complete.", terminal = true)
    public String finishTask(String finalOutput) {
        return finalOutput;
    }
}
