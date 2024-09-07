package com.pine.engine.core.service.primitives.ubo;

import com.pine.engine.core.resource.IResourceRuntimeData;

import java.nio.ByteBuffer;

public record UBORuntimeData(String propertyName, ByteBuffer newData) implements IResourceRuntimeData {
}
