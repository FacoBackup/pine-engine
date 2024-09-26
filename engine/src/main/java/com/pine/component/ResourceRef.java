package com.pine.component;


public class ResourceRef<T> {
    public final String id;
    public transient T resource;

    public ResourceRef(String id) {
        this.id = id;
    }
}
