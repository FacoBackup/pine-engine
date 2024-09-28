package com.pine.component;


import java.io.Serializable;

public class ResourceRef<T> implements Serializable {
    public final String id;
    public transient T resource;

    public ResourceRef(String id) {
        this.id = id;
    }
}
