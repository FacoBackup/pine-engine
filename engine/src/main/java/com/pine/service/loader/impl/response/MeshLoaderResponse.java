package com.pine.service.loader.impl.response;

import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;

import java.util.List;

public class MeshLoaderResponse extends AbstractLoaderResponse<MeshStreamableResource> {
    public MeshLoaderResponse(boolean isLoaded, List<MeshStreamableResource> loadedResources) {
        super(isLoaded, loadedResources);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }

}
