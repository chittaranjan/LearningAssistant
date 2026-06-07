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
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

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

    private byte[] createWord(String content) throws Exception {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(content);
            document.write(baos);
            return baos.toByteArray();
        }
    }

    @Test
    public void testAnalyzeEndpointWithWord() throws Exception {
        byte[] curriculumWord = createWord("Course: Data Science");
        byte[] resumeWord = createWord("Name: Jane Smith");

        MockMultipartFile curriculumFile = new MockMultipartFile(
                "curriculum", "curriculum.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", curriculumWord);
        MockMultipartFile resumeFile = new MockMultipartFile(
                "resume", "resume.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", resumeWord);

        mockMvc.perform(multipart("/api/agent/analyze")
                .file(curriculumFile)
                .file(resumeFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memory").exists())
                .andExpect(jsonPath("$.results").exists());
    }

    @Test
    public void testAnalyzeEndpointMultipleFiles() throws Exception {
        byte[] curriculumPdf1 = createPdf("Course: AI Part 1");
        byte[] curriculumPdf2 = createPdf("Course: AI Part 2");
        byte[] resumeWord = createWord("Name: Bob Brown");

        MockMultipartFile curriculumFile1 = new MockMultipartFile(
                "curriculum", "curriculum1.pdf", "application/pdf", curriculumPdf1);
        MockMultipartFile curriculumFile2 = new MockMultipartFile(
                "curriculum", "curriculum2.pdf", "application/pdf", curriculumPdf2);
        MockMultipartFile resumeFile = new MockMultipartFile(
                "resume", "resume.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", resumeWord);

        mockMvc.perform(multipart("/api/agent/analyze")
                .file(curriculumFile1)
                .file(curriculumFile2)
                .file(resumeFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memory").exists())
                .andExpect(jsonPath("$.results").exists());
    }

    @Test
    public void testAnalyzeEndpointMixed() throws Exception {
        byte[] curriculumPdf = createPdf("Course: AI");
        byte[] resumeWord = createWord("Name: Bob Brown");

        MockMultipartFile curriculumFile = new MockMultipartFile(
                "curriculum", "curriculum.pdf", "application/pdf", curriculumPdf);
        MockMultipartFile resumeFile = new MockMultipartFile(
                "resume", "resume.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", resumeWord);

        mockMvc.perform(multipart("/api/agent/analyze")
                .file(curriculumFile)
                .file(resumeFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memory").exists())
                .andExpect(jsonPath("$.results").exists());
    }
}
