package com.pine.repository.fs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DirectoryEntry implements IEntry, Serializable {
    public final String id = UUID.randomUUID().toString();
    public String name;
    public final DirectoryEntry parent;
    public final List<DirectoryEntry> directories = new ArrayList<>();
    public final List<String> files = new ArrayList<>();

    public DirectoryEntry(String name, DirectoryEntry parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getId() {
        return id;
    }
}
