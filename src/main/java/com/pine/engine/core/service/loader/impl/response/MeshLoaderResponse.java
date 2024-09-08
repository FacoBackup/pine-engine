package com.pine.engine.core.service.loader.impl.response;

import com.pine.engine.core.service.loader.AbstractLoaderResponse;

import java.util.List;

public class MeshLoaderResponse extends AbstractLoaderResponse {
    private  List<MeshInstanceMetadata> meshes;

    public MeshLoaderResponse() {}

    public MeshLoaderResponse(boolean isLoaded, String filePath, List<MeshInstanceMetadata> meshes) {
        super(isLoaded, filePath);
        this.meshes = meshes;
    }

    public List<MeshInstanceMetadata> getMeshes() {
        return meshes;
    }

    public void setMeshes(List<MeshInstanceMetadata> meshes) {
        this.meshes = meshes;
    }
}
