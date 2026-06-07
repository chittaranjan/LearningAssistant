package com.learningAssistant.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningAssistant.core.Memory;
import com.learningAssistant.core.ProgressCallback;
import com.learningAssistant.core.service.AgenticService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*")
public class AgentController {

    @Autowired
    private AgenticService agenticService;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/analyze", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter analyze(@RequestParam("curriculum") MultipartFile[] curriculumFiles,
                              @RequestParam("resume") MultipartFile[] resumeFiles,
                              @RequestParam(value = "prompt", required = false) String customPrompt) throws Exception {
        
        SseEmitter emitter = new SseEmitter(600000L); // 10 minutes timeout

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
        if (customPrompt != null && !customPrompt.isEmpty()) {
            context.put("customPrompt", customPrompt);
        }

        executor.execute(() -> {
            try {
                agenticService.analyze(context, new ProgressCallback() {
                    @Override
                    public void onDecision(String decision) {
                        sendEvent(emitter, "decision", decision);
                    }

                    @Override
                    public void onActionResult(Map<String, Object> result) {
                        sendEvent(emitter, "actionResult", result);
                    }

                    @Override
                    public void onComplete(Memory finalMemory) {
                        Map<String, Object> response = new HashMap<>();
                        List<String> results = new ArrayList<>();
                        
                        // We want to collect the actual results of the analysis and generation tools
                        // The memory contains 'assistant' (JSON tool calls), 'user' (task input or tool results) entries
                        for (Map<String, Object> m : finalMemory.getMemories()) {
                            if ("user".equals(m.get("type"))) {
                                String content = (String) m.get("content");
                                if (content == null) continue;

                                // Filter out the initial user task message
                                if (content.startsWith("Analyze this curriculum text:")) continue;

                                if (!content.startsWith("{")) {
                                    // This is raw text, likely a result from a tool like generateSOP
                                    results.add(content);
                                } else {
                                    // Try to extract the result from JSON
                                    try {
                                        Map<String, Object> map = objectMapper.readValue(content, Map.class);
                                        if (map.containsKey("result") && map.get("result") != null) {
                                            String res = map.get("result").toString();
                                            // Skip errors and nulls
                                            if (!res.equals("null") && !res.startsWith("Error invoking")) {
                                                results.add(res);
                                            }
                                        }
                                    } catch (Exception e) {
                                        // Not JSON or parse error, keep it if it's not a task description
                                        results.add(content);
                                    }
                                }
                            }
                        }
                        
                        response.put("results", results);
                        sendEvent(emitter, "complete", response);
                        emitter.complete();
                    }

                    @Override
                    public void onError(String error) {
                        sendEvent(emitter, "error", error);
                        emitter.completeWithError(new RuntimeException(error));
                    }
                });
            } catch (Exception e) {
                sendEvent(emitter, "error", e.getMessage());
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private void sendEvent(SseEmitter emitter, String name, Object data) {
        try {
            String dataStr = data instanceof String ? (String) data : objectMapper.writeValueAsString(data);
            emitter.send(SseEmitter.event()
                    .name(name)
                    .data(dataStr)
                    .reconnectTime(1000L));
        } catch (Exception e) {
            // Client likely disconnected
        }
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
            // Check if we should even try Tesseract
            Tesseract tesseract = new Tesseract();
            // You might need to set the datapath for Tesseract if it's not in the default location
            // tesseract.setDatapath("/usr/local/share/tessdata");
            
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            return tesseract.doOCR(bufferedImage);
        } catch (NoClassDefFoundError | UnsatisfiedLinkError e) {
            return "OCR Error: Tesseract library not found or not configured in this environment. Please install Tesseract or use PDF/DOCX files.";
        } catch (Exception e) {
            return "Error extracting text from image: " + e.getMessage();
        }
    }
}
