package com.pine.tasks;

import com.pine.annotation.EngineDependency;
import com.pine.annotation.EngineInjectable;
import com.pine.repository.ResourceLoaderRepository;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;

import java.io.File;
import java.util.ArrayList;

import static com.pine.service.resource.ResourceService.MAX_TIMEOUT;

@EngineInjectable
public class ResourceCheckTask extends AbstractTask {
    @EngineDependency
    public ResourceLoaderRepository repository;

    @Override
    protected int getTickIntervalMilliseconds() {
        return MAX_TIMEOUT;
    }

    @Override
    protected void tickInternal() {
        ArrayList<AbstractLoaderResponse> resources = new ArrayList<>(repository.loadedResources);
        resources.forEach(r -> {
            if (!(new File(r.getFilePath())).exists()) {
                repository.loadedResources.remove(r);
            }
        });
    }
}
