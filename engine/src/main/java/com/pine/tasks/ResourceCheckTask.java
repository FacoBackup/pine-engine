package com.pine.tasks;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.ResourceLoaderRepository;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;

import java.io.File;
import java.util.ArrayList;

import static com.pine.service.resource.ResourceService.MAX_TIMEOUT;

@PBean
public class ResourceCheckTask extends AbstractTask {
    @PInject
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
