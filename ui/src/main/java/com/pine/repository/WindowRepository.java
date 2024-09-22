package com.pine.repository;

import com.pine.PBean;

import java.util.HashMap;
import java.util.Map;

@PBean
public class WindowRepository {
    private final Map<String, WindowInstance> instances = new HashMap<>();

    public Map<String, WindowInstance> getInstances() {
        return instances;
    }
}
