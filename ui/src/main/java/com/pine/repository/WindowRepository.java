package com.pine.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class WindowRepository {
    private final Map<String, WindowInstance> instances = new HashMap<>();

    public Map<String, WindowInstance> getInstances() {
        return instances;
    }
}
