package com.pine.service.loader.impl.response;

import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.resource.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MeshLoaderResponse extends AbstractLoaderResponse {
    private  List<MeshInstanceMetadata> meshes;

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PRIMITIVE;
    }

    public MeshLoaderResponse(boolean isLoaded, LoadRequest request, List<MeshInstanceMetadata> meshes) {
        super(isLoaded, request, meshes.stream().map(a -> new AbstractLoaderResponse.ResourceInfo(a.id(), a.name())).collect(Collectors.toList()));
        this.meshes = meshes;
    }

    public List<MeshInstanceMetadata> getMeshes() {
        return meshes;
    }

    public void setMeshes(List<MeshInstanceMetadata> meshes) {
        this.meshes = meshes;
    }
}
