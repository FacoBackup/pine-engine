package com.pine.service.loader.impl.info;

import com.pine.service.resource.resource.ResourceType;

public final class MeshLoaderExtraInfo extends AbstractLoaderExtraInfo {
    private Integer meshIndex;
    private boolean instantiateHierarchy;

    public MeshLoaderExtraInfo(Integer meshIndex) {
        this.meshIndex = meshIndex;
    }

    public MeshLoaderExtraInfo() {
    }

    public Integer getMeshIndex() {
        return meshIndex;
    }

    public MeshLoaderExtraInfo setMeshIndex(Integer meshIndex) {
        this.meshIndex = meshIndex;
        return this;
    }

    public boolean isInstantiateHierarchy() {
        return instantiateHierarchy;
    }

    public MeshLoaderExtraInfo setInstantiateHierarchy(boolean instantiateHierarchy) {
        this.instantiateHierarchy = instantiateHierarchy;
        return this;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PRIMITIVE;
    }
}
