package com.pine.repository.fs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResourceEntry implements Serializable {
    public String name;
    public final ResourceEntryType type;
    public final float size;
    public final long creationDate ;
    public final String path;
    public List<ResourceEntry> children = new ArrayList<>();

    public ResourceEntry(ResourceEntryType type, float size, String path, long creationDate) {
        this.type = type;
        this.size = size;
        this.path = path;
        this.creationDate = creationDate;
    }
}
