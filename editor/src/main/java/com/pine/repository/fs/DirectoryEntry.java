package com.pine.repository.fs;

import java.io.Serializable;
import java.util.*;

public class DirectoryEntry implements IEntry, Serializable {
    public final String id;
    public String name;
    public Set<String> files = new HashSet<>();

    public DirectoryEntry(String name) {
        this.name = name;
        id = UUID.randomUUID().toString();
    }

    public DirectoryEntry(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public String getId() {
        return id;
    }
}
