package com.learningAssistant.analysis;

import com.learningAssistant.core.LLMProvider;
import com.learningAssistant.core.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MockAnalysisProviderTest {

    @Test
    public void testGenerateCompletionSequence() {
        MockAnalysisProvider provider = new MockAnalysisProvider();
        List<Message> messages = new ArrayList<>();
        LLMProvider.Context context = new LLMProvider.Context(messages);
        
        // Step 0: readPdfContent (curriculum)
        LLMProvider.Result result0 = provider.generateCompletion(context);
        assertTrue(result0.getContent().contains("readPdfContent"));
        assertTrue(result0.getContent().contains("curriculum.pdf"));
        
        // Step 1: readPdfContent (resume)
        LLMProvider.Result result1 = provider.generateCompletion(context);
        assertTrue(result1.getContent().contains("readPdfContent"));
        assertTrue(result1.getContent().contains("resume.pdf"));
        
        // Step 2: evaluateCurriculum
        LLMProvider.Result result2 = provider.generateCompletion(context);
        assertTrue(result2.getContent().contains("evaluateCurriculum"));
        
        // Step 3: evaluateResume
        LLMProvider.Result result3 = provider.generateCompletion(context);
        assertTrue(result3.getContent().contains("evaluateResume"));
        
        // Step 4: generateSOP
        LLMProvider.Result result4 = provider.generateCompletion(context);
        assertTrue(result4.getContent().contains("generateSOP"));
        
        // Step 5: generateStudyPlan
        LLMProvider.Result result5 = provider.generateCompletion(context);
        assertTrue(result5.getContent().contains("generateStudyPlan"));
        
        // Default: finishTask
        LLMProvider.Result resultFinal = provider.generateCompletion(context);
        assertTrue(resultFinal.getContent().contains("finishTask"));
    }
}
