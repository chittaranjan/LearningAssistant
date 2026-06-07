package com.learningAssistant.api;

import com.learningAssistant.core.service.AgenticService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*")
public class AgentController {

    @Autowired
    private AgenticService agenticService;

    @PostMapping("/analyze")
    public Map<String, Object> analyze(@RequestParam("curriculum") MultipartFile curriculumFile,
                                       @RequestParam("resume") MultipartFile resumeFile) throws Exception {
        
        String curriculumText = extractText(curriculumFile);
        String resumeText = extractText(resumeFile);

        Map<String, Object> context = new HashMap<>();
        context.put("curriculumText", curriculumText);
        context.put("resumeText", resumeText);

        return agenticService.analyze(context);
    }

    private String extractText(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
