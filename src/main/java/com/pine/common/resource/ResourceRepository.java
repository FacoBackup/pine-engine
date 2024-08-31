package com.pine.common.resource;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ResourceRepository {
    /**
     * 5 minutes
     */
    public static final long MAX_TIMEOUT = 5 * 60 * 1000;
    private final Map<String, IResource> resources = new HashMap<>();
    private final Map<String, Long> sinceLastUse = new HashMap<>();
    private final Map<ResourceType, List<String>> usedResources = new HashMap<>();

    public Map<ResourceType, List<String>> getUsedResources() {
        return usedResources;
    }

    public Map<String, Long> getSinceLastUse() {
        return sinceLastUse;
    }

    public Map<String, IResource> getResources() {
        return resources;
    }
}
