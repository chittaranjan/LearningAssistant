package com.learningAssistant.analysis;

import com.learningAssistant.core.RegisterTool;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class AnalysisTools {

    @RegisterTool(name = "readFileContent", description = "Reads text content from a PDF, Word (.docx), or Image file given its path.")
    public static String readFileContent(String filePath) {
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
    public static String evaluateCurriculum(String curriculumText) {
        if (curriculumText == null || curriculumText.trim().isEmpty()) {
            throw new RuntimeException("Curriculum text is empty. Did you forget to read the curriculum files?");
        }
        // In a real system, this might call another specialized LLM prompt or a service.
        // For this demonstration, we'll return a simulated analysis.
        return "Curriculum Analysis Result:\n" +
               "- Key Outcomes: Advanced Machine Learning, Neural Networks, Distributed Systems.\n" +
               "- Prerequisites: Linear Algebra, Probability, Java/Python proficiency.\n" +
               "- Intensity: High";
    }

    @RegisterTool(name = "evaluateResume", description = "Analyzes a resume and returns candidate skills and experience.")
    public static String evaluateResume(String resumeText) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new RuntimeException("Resume text is empty. Did you forget to read the resume files?");
        }
        return "Resume Analysis Result:\n" +
               "- Core Skills: Java, Spring Boot, Basic Calculus.\n" +
               "- Experience: 2 years Software Development.\n" +
               "- Gaps: Lacks advanced ML experience.";
    }

    @RegisterTool(name = "generateSOP", description = "Generates a Statement of Purpose based on curriculum analysis and resume analysis.")
    public static String generateSOP(String curriculumAnalysis, String resumeAnalysis) {
        return "Statement of Purpose:\n" +
               "I am excited to apply for this course because my background in " + resumeAnalysis + 
               " aligns with the curriculum focusing on " + curriculumAnalysis + ". " +
               "I aim to bridge my gaps in ML through this program.";
    }

    @RegisterTool(name = "generateStudyPlan", description = "Generates a study plan to prepare for the course based on curriculum and resume analysis.")
    public static String generateStudyPlan(String curriculumAnalysis, String resumeAnalysis) {
        return "Study Plan:\n" +
               "1. Review Linear Algebra (Prerequisite).\n" +
               "2. Take an introductory course in Machine Learning to address gaps.\n" +
               "3. Practice distributed systems concepts.";
    }
    
    @RegisterTool(name = "finishTask", description = "Call this when the task is complete.", terminal = true)
    public static String finishTask(String finalOutput) {
        System.out.println("TASK COMPLETE: " + finalOutput);
        return finalOutput;
    }
}
