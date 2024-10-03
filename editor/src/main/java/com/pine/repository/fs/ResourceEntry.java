package com.pine.repository.fs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResourceEntry implements Serializable {
    public String name;
    public final ResourceEntryType type;
    public final float size;
    public final long creationDate = System.currentTimeMillis();
    public final String absolutePath;
    public List<ResourceEntry> children = new ArrayList<>();

    public ResourceEntry(ResourceEntryType type, float size, String absolutePath) {
        this.type = type;
        this.size = size;
        this.absolutePath = absolutePath;
    }
}
