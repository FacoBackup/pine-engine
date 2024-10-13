package com.pine.service.resource;

import com.pine.injection.Disposable;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PBean
public class ResourceService implements Loggable, Disposable {

    @PInject
    public List<AbstractResourceService> implementations;

    private final Map<String, IResource> resources = new HashMap<>();

    public IResource addResource(ResourceCreationData data) {
        String fixedId = UUID.randomUUID().toString();
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
        return instance;
    }

    @Override
    public void dispose() {
        resources.values().forEach(Disposable::dispose);
    }

    public void remove(String id) {
        IResource iResource = resources.get(id);
        if(iResource != null) {
            iResource.dispose();
            resources.remove(id);
        }
    }
}
