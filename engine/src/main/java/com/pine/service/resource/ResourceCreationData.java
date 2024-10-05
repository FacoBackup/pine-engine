package com.pine.service.resource;

public abstract class ResourceCreationData {
    private boolean isStaticResource;

    public abstract LocalResourceType getResourceType();

    public ResourceCreationData staticResource(){
        isStaticResource = true;
        return this;
    }

    public boolean isStaticResource() {
        return isStaticResource;
    }
}
