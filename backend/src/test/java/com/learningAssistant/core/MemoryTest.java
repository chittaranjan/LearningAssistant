package com.learningAssistant.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryTest {

    @Test
    public void testAddMemory() {
        Memory memory = new Memory();
        Map<String, Object> item = new HashMap<>();
        item.put("type", "user");
        item.put("content", "Hello");
        
        memory.addMemory(item);
        
        List<Map<String, Object>> memories = memory.getMemories();
        assertEquals(1, memories.size());
        assertEquals("user", memories.get(0).get("type"));
        assertEquals("Hello", memories.get(0).get("content"));
    }

    @Test
    public void testGetMemoriesWithLimit() {
        Memory memory = new Memory();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("index", i);
            memory.addMemory(item);
        }
        
        assertEquals(5, memory.getMemories().size());
        assertEquals(2, memory.getMemories(2).size());
        assertEquals(5, memory.getMemories(10).size());
        assertEquals(0, memory.getMemories(0).size());
    }
}
