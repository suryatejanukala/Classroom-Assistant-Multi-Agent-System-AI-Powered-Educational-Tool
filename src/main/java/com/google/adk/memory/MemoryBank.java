package com.google.adk.memory;

import java.util.*;

public class MemoryBank {
    private Map<String, Object> storage = new HashMap<>();
    
    public void store(String key, Object value) {
        storage.put(key, value);
    }
    
    public Object retrieve(String key) {
        return storage.get(key);
    }
    
    public Set<String> getAllKeys() {
        return storage.keySet();
    }
    
    public void remove(String key) {
        storage.remove(key);
    }
    
    public void clear() {
        storage.clear();
    }
}