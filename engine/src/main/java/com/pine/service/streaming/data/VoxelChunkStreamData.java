package com.pine.service.streaming.data;

import com.pine.repository.streaming.StreamableResourceType;

import java.nio.IntBuffer;

public record VoxelChunkStreamData(IntBuffer buffer) implements StreamData {

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.VOXEL_CHUNK;
    }
}
