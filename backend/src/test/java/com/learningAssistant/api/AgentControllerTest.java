package com.learningAssistant.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

@SpringBootTest
@AutoConfigureMockMvc
public class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private byte[] createPdf(String content) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(content);
                contentStream.endText();
            }
            document.save(baos);
            return baos.toByteArray();
        }
    }

    @Test
    public void testAnalyzeEndpoint() throws Exception {
        byte[] curriculumPdf = createPdf("Course: Computer Science");
        byte[] resumePdf = createPdf("Name: John Doe");

        MockMultipartFile curriculumFile = new MockMultipartFile(
                "curriculum", "curriculum.pdf", "application/pdf", curriculumPdf);
        MockMultipartFile resumeFile = new MockMultipartFile(
                "resume", "resume.pdf", "application/pdf", resumePdf);

        mockMvc.perform(multipart("/api/agent/analyze")
                .file(curriculumFile)
                .file(resumeFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memory").exists())
                .andExpect(jsonPath("$.results").exists());
    }
}
