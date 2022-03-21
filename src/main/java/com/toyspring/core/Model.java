package com.toyspring.core;


import java.util.HashMap;
import java.util.Map;

public class Model {

    private final Map<String, String> map  = new HashMap<>();
    
    public void addAttribute(String name, String value) {
        map.put(name, value);
    }

    public String getAttribute(String name) {
        return map.get(name);
    }

    @Override
    public String toString() {
        return "Model{" + map + '}';
    }
}


