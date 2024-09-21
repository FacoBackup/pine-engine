package com.pine.service.resource.resource;

public abstract class ResourceCreationData {
    private boolean isStaticResource;

    public abstract ResourceType getResourceType();

    public ResourceCreationData staticResource(){
        isStaticResource = true;
        return this;
    }

    public boolean isStaticResource() {
        return isStaticResource;
    }
}
