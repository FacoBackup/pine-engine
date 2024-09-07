package com.pine.engine.service;

import com.pine.common.Loggable;
import com.pine.engine.resource.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pine.engine.resource.ResourceRepository.MAX_TIMEOUT;

public class ResourceService implements Loggable {
    private final ResourceRepository repository = new ResourceRepository();
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
        repository.getResources().put(instance.getId(), instance);
        repository.getSinceLastUse().put(instance.getId(), System.currentTimeMillis());
        return instance;
    }

    public void removeResource(String id) {
        IResource resource = repository.getResources().get(id);
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
        return repository.getResources().values().stream().filter(r -> r.getResourceType().equals(type)).collect(Collectors.toList());
    }

    public void shutdown() {
        implementations.forEach(i -> i.shutdown(getAllByType(i.getResourceType())));
    }

    public void removeUnused(long totalTime) {
        if((totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = totalTime;
            int removed = 0;
            for (var entry : repository.getSinceLastUse().entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() > MAX_TIMEOUT) {
                    removeResource(entry.getKey());
                    removed++;
                }
            }
            getLogger().warn("Removed {} unused resources", removed);
            repository.getUsedResources().clear();
            repository.getResources().values().forEach(resource -> {
                repository.getUsedResources().putIfAbsent(resource.getResourceType(), new ArrayList<>());
                repository.getUsedResources().get(resource.getResourceType()).add(resource.getId());
            });
        }
    }

    public void onInitialize() {
        implementations.forEach(AbstractResourceService::onInitialize);
    }
}
