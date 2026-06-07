package com.learningAssistant.analysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnalysisToolsTest {

    @Test
    public void testEvaluateCurriculum() {
        String result = AnalysisTools.evaluateCurriculum("CS101");
        assertNotNull(result);
        assertTrue(result.contains("Curriculum Analysis Result"));
        assertTrue(result.contains("Machine Learning"));
    }

    @Test
    public void testEvaluateResume() {
        String result = AnalysisTools.evaluateResume("John Doe's Resume");
        assertNotNull(result);
        assertTrue(result.contains("Resume Analysis Result"));
        assertTrue(result.contains("Java"));
    }

    @Test
    public void testGenerateSOP() {
        String result = AnalysisTools.generateSOP("ML background", "Software dev background");
        assertNotNull(result);
        assertTrue(result.contains("Statement of Purpose"));
    }

    @Test
    public void testGenerateStudyPlan() {
        String result = AnalysisTools.generateStudyPlan("ML background", "Software dev background");
        assertNotNull(result);
        assertTrue(result.contains("Study Plan"));
    }

    @Test
    public void testFinishTask() {
        String result = AnalysisTools.finishTask("Done");
        assertEquals("Done", result);
    }
}
