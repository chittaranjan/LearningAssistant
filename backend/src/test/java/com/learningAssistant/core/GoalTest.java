package com.learningAssistant.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GoalTest {

    @Test
    public void testGoalProperties() {
        Goal goal = new Goal(1, "Test Goal", "This is a test goal description");
        
        assertEquals(1, goal.getPriority());
        assertEquals("Test Goal", goal.getName());
        assertEquals("This is a test goal description", goal.getDescription());
    }
}
