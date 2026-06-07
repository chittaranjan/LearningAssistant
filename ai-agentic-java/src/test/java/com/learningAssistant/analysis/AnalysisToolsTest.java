package com.learningAssistant.analysis;

import com.learningAssistant.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnalysisToolsTest {

    private AnalysisTools tools;
    private LLM mockLlm;

    @BeforeEach
    public void setup() {
        mockLlm = new LLM(context -> new LLMProvider.Result("Mock LLM Response", 10), 
                          new TokenManager() {
                              @Override public long getTotalTokensUsed() { return 0; }
                              @Override public void addTokens(long tokens) {}
                          }, 
                          () -> {});
        tools = new AnalysisTools(mockLlm, "Custom instruction");
    }

    @Test
    public void testEvaluateCurriculum() {
        String result = tools.evaluateCurriculum("CS101");
        assertNotNull(result);
        assertEquals("Mock LLM Response", result);
    }

    @Test
    public void testEvaluateResume() {
        String result = tools.evaluateResume("John Doe's Resume");
        assertNotNull(result);
        assertEquals("Mock LLM Response", result);
    }

    @Test
    public void testGenerateSOP() {
        String result = tools.generateSOP("ML background", "Software dev background");
        assertNotNull(result);
        assertEquals("Mock LLM Response", result);
    }

    @Test
    public void testGenerateStudyPlan() {
        String result = tools.generateStudyPlan("ML background", "Software dev background");
        assertNotNull(result);
        assertEquals("Mock LLM Response", result);
    }

    @Test
    public void testFinishTask() {
        String result = tools.finishTask("Done");
        assertEquals("Done", result);
    }
}
