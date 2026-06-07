package com.learningAssistant.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class ActionTest {

    @Test
    public void testActionProperties() {
        Map<String, Object> params = new HashMap<>();
        params.put("arg1", "string");
        Tool tool = new Tool("testTool", "A test tool", params, true);
        
        Action action = new Action(tool);
        
        assertEquals("testTool", action.getToolName());
        assertEquals("A test tool", action.getDescription());
        assertEquals(params, action.getParameters());
        assertTrue(action.isTerminal());
        assertEquals(tool, action.getTool());
    }

    @Test
    public void testActionArgs() {
        Tool tool = new Tool("testTool", "A test tool", new HashMap<>());
        Action action = new Action(tool);
        
        Map<String, Object> args = new HashMap<>();
        args.put("input", "hello");
        action.setArgs(args);
        
        assertEquals(args, action.getArgs());
    }
}
