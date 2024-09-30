package com.pine.service.resource;

import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.ClockRepository;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.resource.resource.*;
import com.pine.tasks.SyncTask;

import java.util.*;
import java.util.stream.Collectors;

@PBean
public class ResourceService implements Loggable, SyncTask {
    public static final int MAX_TIMEOUT = 5 * 60 * 1000;

    @PInject
    public List<AbstractResourceService> implementations;

    @PInject
    public ClockRepository clock;

    @PInject
    public ResourceLoaderService loader;

    private final Map<String, IResource> resources = new HashMap<>();
    private final Map<String, Long> sinceLastUse = new HashMap<>();
    private final Map<ResourceType, List<String>> usedResources = new HashMap<>();
    private long sinceLastCleanup;
    private final List<String> schedule = new ArrayList<>();

    public IResource addResource(ResourceCreationData data) {
        return addResource(data, UUID.randomUUID().toString());
    }

    public IResource addResource(ResourceCreationData data, String fixedId) {
        IResource instance = null;
        for (var i : implementations) {
            if (i.getResourceType() == data.getResourceType()) {
                instance = i.add(data, fixedId);
            }
        }
        if (instance == null) {
            getLogger().warn("Resource could not be initialized correctly: {}", data.getResourceType());
            return null;
        }
        resources.put(fixedId, instance);
        sinceLastUse.put(instance.getId(), System.currentTimeMillis());
        return instance;
    }

    public void removeResource(String id) {
        IResource resource = resources.get(id);
        if (resource == null) {
            getLogger().warn("Resource not found: {}", id);
            return;
        }

        if (resource.isStatic()) {
            return;
        }

        for (var i : implementations) {
            if (i.getResourceType() == resource.getResourceType()) {
                i.remove(resource);
            }
        }
        resources.remove(id);
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

    @Override
    public void sync() {
        if ((clock.totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = clock.totalTime;
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

        if (!schedule.isEmpty()) {
            getLogger().warn("Loading {} scheduled resources", schedule.size());
            for (int i = 0, scheduleSize = schedule.size(); i < scheduleSize; i++) {
                String id = schedule.get(i);
                if (resources.containsKey(id)) {
                    continue;
                }
                List<AbstractLoaderResponse> loadedResources = loader.repository.loadedResources;
                i:
                for (int j = 0, loadedResourcesSize = loadedResources.size(); j < loadedResourcesSize; j++) {
                    var history = loadedResources.get(j);
                    for (var record : history.records) {
                        if (Objects.equals(record.id, id)) {
                            AbstractLoaderResponse load = loader.load(history.request);
                            if (load == null) {
                                getLogger().error("Could not load resource: {}", history.request.path());
                            }
                            break i;
                        }
                    }
                }
            }
            schedule.clear();
        }
    }

    public IResource getById(String id) {
        return resources.get(id);
    }

    public void makeStatic(IResource resource) {
        resource.makeStatic();
    }

    public IResource getOrCreateResource(String id) {
        IResource found = getById(id);
        if (found != null) {
            return found;
        }

        if (!schedule.contains(id)) {
            getLogger().warn("Scheduling load of resource {}", id);
            schedule.add(id);
        }

        return null;
    }
}
