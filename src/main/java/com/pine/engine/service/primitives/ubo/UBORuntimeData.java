package com.pine.engine.service.primitives.ubo;

import com.pine.common.resource.IResourceRuntimeData;

import java.nio.ByteBuffer;

public record UBORuntimeData(String propertyName, ByteBuffer newData) implements IResourceRuntimeData {
}
