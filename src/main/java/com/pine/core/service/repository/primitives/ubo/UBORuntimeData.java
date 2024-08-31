package com.pine.core.service.repository.primitives.ubo;

import com.pine.core.service.common.IResourceRuntimeData;

import java.nio.ByteBuffer;

public record UBORuntimeData(String propertyName, ByteBuffer newData) implements IResourceRuntimeData {
}
