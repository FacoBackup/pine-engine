package com.pine.service.streaming.data;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamData;

public record EnvironmentMapStreamData(EnvironmentMapLOD[] lod) implements StreamData {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.ENVIRONMENT_MAP;
    }
}
