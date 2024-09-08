package com.pine.engine.core.service.resource.primitives.ubo;

import com.pine.engine.core.service.resource.resource.IResourceRuntimeData;

import java.nio.ByteBuffer;

public record UBORuntimeData(String propertyName, ByteBuffer newData) implements IResourceRuntimeData {
}
