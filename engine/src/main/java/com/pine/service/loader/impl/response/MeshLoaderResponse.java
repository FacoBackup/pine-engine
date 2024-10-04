package com.pine.service.loader.impl.response;

import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.loader.impl.info.LoadRequest;

import java.util.List;

public class MeshLoaderResponse extends AbstractLoaderResponse<MeshStreamableResource> {
    public MeshLoaderResponse(boolean isLoaded, LoadRequest request, List<MeshStreamableResource> loadedResources) {
        super(isLoaded, request, loadedResources);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }

}
