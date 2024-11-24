package com.pine.engine.service.streaming.data;

import com.pine.engine.repository.streaming.StreamableResourceType;

import java.nio.IntBuffer;

public record VoxelChunkStreamData(IntBuffer buffer) implements StreamData {

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.VOXEL_CHUNK;
    }
}
