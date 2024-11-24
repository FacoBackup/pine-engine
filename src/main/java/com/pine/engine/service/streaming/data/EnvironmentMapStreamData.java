package com.pine.engine.service.streaming.data;

import com.pine.engine.repository.streaming.StreamableResourceType;

import java.nio.ByteBuffer;

public record EnvironmentMapStreamData(int imageSize, ByteBuffer[] images) implements StreamData {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.ENVIRONMENT_MAP;
    }
}
