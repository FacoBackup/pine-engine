package com.pine.service.streaming.data;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamData;

import java.nio.ByteBuffer;

public record EnvironmentMapStreamData(int imageSize, ByteBuffer[] images) implements StreamData {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.ENVIRONMENT_MAP;
    }
}
