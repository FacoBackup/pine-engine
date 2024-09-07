package com.pine.engine.service;

import com.pine.common.Loggable;
import com.pine.engine.resource.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
public class ResourceService implements Loggable {
    public static final long MAX_TIMEOUT = 5 * 60 * 1000;
    private final Map<String, IResource> resources = new HashMap<>();
    private final Map<String, Long> sinceLastUse = new HashMap<>();
    private final Map<ResourceType, List<String>> usedResources = new HashMap<>();
    private final List<AbstractResourceService<? extends IResource, ? extends IResourceRuntimeData, ? extends IResourceCreationData>> implementations;
    private long sinceLastCleanup = 0;

    public ResourceService(List<AbstractResourceService<? extends IResource, ? extends IResourceRuntimeData, ? extends IResourceCreationData>> impls) {
        this.implementations = impls;
    }

    public IResource addResource(IResourceCreationData data) {
        IResource instance = null;
        for (var i : implementations) {
            if (i.getResourceType() == data.getResourceType()) {
                instance = i.add(data);
            }
        }
        if (instance == null) {
            getLogger().warn("Resource could not be initialized correctly: {}", data.getResourceType());
            return null;
        }
        resources.put(instance.getId(), instance);
        sinceLastUse.put(instance.getId(), System.currentTimeMillis());
        return instance;
    }

    public void removeResource(String id) {
        IResource resource = resources.get(id);
        if (resource == null) {
            getLogger().warn("Resource not found: {}", id);
            return;
        }
        for (var i : implementations) {
            if (i.getResourceType() == resource.getResourceType()) {
                i.remove(resource);
            }
        }
    }

    public <T extends IResource, R extends IResourceRuntimeData> void bind(T instance, R data) {
        for (var i : implementations) {
            if (i.getResourceType() == instance.getResourceType()) {
                i.bind(instance, data);
            }
        }
    }

    public <T extends IResource> void bind(T instance) {
        for (var i : implementations) {
            if (i.getResourceType() == instance.getResourceType()) {
                i.bind(instance);
            }
        }
    }

    public List<IResource> getAllByType(ResourceType type) {
        return resources.values().stream().filter(r -> r.getResourceType().equals(type)).collect(Collectors.toList());
    }

    public void shutdown() {
        implementations.forEach(i -> i.shutdown(getAllByType(i.getResourceType())));
    }

    public void removeUnused(long totalTime) {
        if((totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = totalTime;
            int removed = 0;
            for (var entry : sinceLastUse.entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() > MAX_TIMEOUT) {
                    removeResource(entry.getKey());
                    removed++;
                }
            }
            getLogger().warn("Removed {} unused resources", removed);
            usedResources.clear();
            resources.values().forEach(resource -> {
                usedResources.putIfAbsent(resource.getResourceType(), new ArrayList<>());
                usedResources.get(resource.getResourceType()).add(resource.getId());
            });
        }
    }

    public void onInitialize() {
        implementations.forEach(AbstractResourceService::onInitialize);
    }
}
