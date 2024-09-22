package com.pine.service.loader.impl.response;

import com.pine.service.serialization.SerializableResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable information relating to file, used for dropdown selection at runtime of textures/meshes etc. since some files include more information
 * than only one instance.
 * Also includes basic information like file path and if the loading process was successful
 */
public abstract class AbstractLoaderResponse implements SerializableResource {
    public static class ResourceInfo{
        public final String id;
        public final String name;

        public ResourceInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    private boolean isLoaded;
    private String filePath;
    private final List<ResourceInfo> records = new ArrayList<>();

    public AbstractLoaderResponse() {
    }

    public AbstractLoaderResponse(boolean isLoaded, String filePath, List<ResourceInfo> records) {
        this.isLoaded = isLoaded;
        this.filePath = filePath;
        this.records.addAll(records);
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public List<ResourceInfo> getRecords() {
        return records;
    }
}
