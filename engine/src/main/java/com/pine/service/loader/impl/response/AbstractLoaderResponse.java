package com.pine.service.loader.impl.response;

import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.resource.resource.ResourceType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializable information relating to file, used for dropdown selection at runtime of textures/meshes etc. since some files include more information
 * than only one instance.
 * Also includes basic information like file path and if the loading process was successful
 */
public abstract class AbstractLoaderResponse implements Serializable {
    public abstract ResourceType getResourceType();

    public static class ResourceInfo implements Serializable {
        public final String id;
        public final String name;

        public ResourceInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public final boolean isLoaded;
    public final LoadRequest request;
    public final ArrayList<ResourceInfo> records;

    public AbstractLoaderResponse(boolean isLoaded, LoadRequest request, List<ResourceInfo> records) {
        this.isLoaded = isLoaded;
        this.request = request;
        this.records = new ArrayList<>(records);
    }
}
