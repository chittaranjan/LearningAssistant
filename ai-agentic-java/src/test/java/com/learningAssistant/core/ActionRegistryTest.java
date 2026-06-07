package com.learningAssistant.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistryTest {

    @Test
    public void testRegisterAndGetAction() {
        ActionRegistry registry = new ActionRegistry();
        Tool tool = new Tool("testTool", "Description", new HashMap<>());
        Object binding = new Object();
        
        registry.register(tool, binding);
        
        Action action = registry.getAction("testTool");
        assertNotNull(action);
        assertEquals("testTool", action.getToolName());
        assertEquals(binding, registry.getBinding("testTool"));
    }

    @Test
    public void testGetActionWithArgs() {
        ActionRegistry registry = new ActionRegistry();
        Tool tool = new Tool("testTool", "Description", new HashMap<>());
        registry.register(tool, null);
        
        Map<String, Object> args = new HashMap<>();
        args.put("key", "value");
        Action action = registry.getAction("testTool", args);
        
        assertNotNull(action);
        assertEquals(args, action.getArgs());
    }

    @Test
    public void testGetNonExistentAction() {
        ActionRegistry registry = new ActionRegistry();
        assertNull(registry.getAction("unknown"));
    }
}
