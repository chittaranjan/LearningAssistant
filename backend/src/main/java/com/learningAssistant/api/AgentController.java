package com.learningAssistant.api;

import com.learningAssistant.core.service.AgenticService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*")
public class AgentController {

    @Autowired
    private AgenticService agenticService;

    @PostMapping("/analyze")
    public Map<String, Object> analyze(@RequestParam("curriculum") MultipartFile[] curriculumFiles,
                                       @RequestParam("resume") MultipartFile[] resumeFiles) throws Exception {
        
        StringBuilder curriculumText = new StringBuilder();
        for (MultipartFile file : curriculumFiles) {
            curriculumText.append(extractText(file)).append("\n\n");
        }

        StringBuilder resumeText = new StringBuilder();
        for (MultipartFile file : resumeFiles) {
            resumeText.append(extractText(file)).append("\n\n");
        }

        Map<String, Object> context = new HashMap<>();
        context.put("curriculumText", curriculumText.toString().trim());
        context.put("resumeText", resumeText.toString().trim());

        return agenticService.analyze(context);
    }

    private String extractText(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) return "";
        filename = filename.toLowerCase();

        if (filename.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else if (filename.endsWith(".docx")) {
            return extractTextFromWord(file);
        } else if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return extractTextFromImage(file);
        } else {
            // Fallback: try to read as plain text if it's not a known binary format
            return new String(file.getBytes());
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextFromWord(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }

    private String extractTextFromImage(MultipartFile file) {
        try {
            Tesseract tesseract = new Tesseract();
            // You might need to set the datapath for Tesseract if it's not in the default location
            // tesseract.setDatapath("/usr/local/share/tessdata");
            
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            return tesseract.doOCR(bufferedImage);
        } catch (Exception e) {
            return "Error extracting text from image: " + e.getMessage() + ". (Note: Tesseract OCR might not be fully configured in this environment)";
        }
    }
}
