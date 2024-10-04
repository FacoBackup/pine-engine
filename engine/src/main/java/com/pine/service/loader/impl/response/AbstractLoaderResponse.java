package com.pine.service.loader.impl.response;

import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;

import java.io.Serializable;
import java.util.List;

/**
 * Serializable information relating to file, used for dropdown selection at runtime of textures/meshes etc. since some files include more information
 * than only one instance.
 * Also includes basic information like file path and if the loading process was successful
 */
public abstract class AbstractLoaderResponse<T extends AbstractStreamableResource<?>> implements Serializable {
    public abstract StreamableResourceType getResourceType();

    public final boolean isLoaded;
    public final List<T> loadedResources;

    public AbstractLoaderResponse(boolean isLoaded, List<T> loadedResources) {
        this.isLoaded = isLoaded;
        this.loadedResources = loadedResources;
    }
}
