package com.pine.service.resource;

import com.pine.Loggable;
import com.pine.injection.Disposable;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.ClockRepository;
import com.pine.service.loader.LoaderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PBean
public class ResourceService implements Loggable, Disposable {

    @PInject
    public List<AbstractResourceService<?, ?>> implementations;

    @PInject
    public ClockRepository clock;

    @PInject
    public LoaderService loader;

    private final Map<String, IResource> resources = new HashMap<>();

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

    @Override
    public void dispose() {
        resources.values().forEach(Disposable::dispose);
    }

}
