package com.pine.repository.fs;


import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.MeshStreamableResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResourceEntry implements Serializable {
    public String name;
    public final ResourceEntryType type;
    public final float size;
    public final long creationDate = System.currentTimeMillis();
    public final String path;
    public List<ResourceEntry> children = new ArrayList<>();
    public ResourceEntry parent = null;
    public final String sizeText;
    public AbstractStreamableResource<?> streamableResource;

    public ResourceEntry(String name, ResourceEntryType type, float size, String path, ResourceEntry parent, AbstractStreamableResource<?> streamableResource) {
        this.name = name;
        this.streamableResource = streamableResource;
        this.type = type;
        this.size = size;
        this.path = path;
        this.parent = parent;
        String sizeUnit = "mb";
        double fileSize = (double) size / (1024 * 1024);
        if (fileSize > 1000) {
            fileSize = fileSize / 1024;
            sizeUnit = "gb";
        }
        if (fileSize < 1) {
            fileSize = (double) size / 1024;
            sizeUnit = "kb";
        }
        sizeText = String.format("%.2f", fileSize) + sizeUnit;
    }
}
