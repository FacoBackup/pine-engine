package com.pine.engine.core.service.loader.impl.response;

import com.pine.engine.core.service.serialization.SerializableResource;

import java.util.UUID;

/**
 * Serializable information relating to file, used for dropdown selection at runtime of textures/meshes etc. since some files include more information
 * than only one instance.
 * Also includes basic information like file path and if the loading process was successful
 */
public abstract class AbstractLoaderResponse implements SerializableResource {
    private boolean isLoaded;
    private String filePath;

    public AbstractLoaderResponse() {
    }

    public AbstractLoaderResponse(boolean isLoaded, String filePath) {
        this.isLoaded = isLoaded;
        this.filePath = filePath;
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
}
