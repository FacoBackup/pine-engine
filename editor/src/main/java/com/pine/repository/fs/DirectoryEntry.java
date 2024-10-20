package com.pine.repository.fs;

import java.io.Serializable;
import java.util.*;

public class DirectoryEntry implements IEntry, Serializable {
    public final String id = UUID.randomUUID().toString();
    public String name;
    public final DirectoryEntry parent;
    public final Map<String, DirectoryEntry> directories = new HashMap<>();
    public Set<String> files = new HashSet<>();

    public DirectoryEntry(String name, DirectoryEntry parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getId() {
        return id;
    }
}
