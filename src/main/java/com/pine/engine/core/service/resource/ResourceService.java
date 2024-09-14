package com.pine.engine.core.service.resource;

import com.pine.common.Updatable;
import com.pine.common.Initializable;
import com.pine.common.Loggable;
import com.pine.engine.core.repository.ClockRepository;
import com.pine.engine.core.service.resource.resource.*;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EngineInjectable
public class ResourceService implements Loggable, Updatable {
    public static final long MAX_TIMEOUT = 5 * 60 * 1000;

    @EngineDependency
    public ClockRepository clock;

    @EngineDependency
    public AudioService implAudioService;

    @EngineDependency
    public MeshService implMeshService;

    @EngineDependency
    public ShaderService implShaderService;

    @EngineDependency
    public TextureService implTextureService;

    @EngineDependency
    public UBOService implUBOService;

    @EngineDependency
    public FBOService implFBOService;

    private final List<AbstractResourceService<?, ?, ?>> implementations = new ArrayList<>();
    private final Map<String, IResource> resources = new HashMap<>();
    private final Map<String, Long> sinceLastUse = new HashMap<>();
    private final Map<ResourceType, List<String>> usedResources = new HashMap<>();
    private long sinceLastCleanup = 0;

    public List<AbstractResourceService<?, ?, ?>> getImplementations() {
        if(implementations.isEmpty()){
            implementations.add(implAudioService);
            implementations.add(implMeshService);
            implementations.add(implShaderService);
            implementations.add(implTextureService);
            implementations.add(implUBOService);
            implementations.add(implFBOService);
        }
        return implementations;
    }

    public IResource addResource(ResourceCreationData data) {
        IResource instance = null;
        for (var i : getImplementations()) {
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

        if (resource.isStatic()) {
            return;
        }

        for (var i : getImplementations()) {
            if (i.getResourceType() == resource.getResourceType()) {
                i.remove(resource);
            }
        }
        resources.remove(id);
    }

    public <T extends IResource, R extends IResourceRuntimeData> void bind(T instance, R data) {
        for (var i : getImplementations()) {
            if (i.getResourceType() == instance.getResourceType()) {
                i.bind(instance, data);
            }
        }
    }

    public <T extends IResource> void bind(T instance) {
        for (var i : getImplementations()) {
            if (i.getResourceType() == instance.getResourceType()) {
                i.bind(instance);
            }
        }
    }

    public List<IResource> getAllByType(ResourceType type) {
        return resources.values().stream().filter(r -> r.getResourceType().equals(type)).collect(Collectors.toList());
    }

    public void shutdown() {
        getImplementations().forEach(i -> i.shutdown(getAllByType(i.getResourceType())));
    }

    @Override
    public void tick() {
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
    }

    public IResource getById(String id) {
        return resources.get(id);
    }
}
