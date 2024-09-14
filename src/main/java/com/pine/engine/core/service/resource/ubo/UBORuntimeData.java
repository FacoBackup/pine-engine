package com.pine.engine.core.service.resource.ubo;

import com.pine.engine.core.service.resource.resource.IResourceRuntimeData;

import java.nio.FloatBuffer;

public record UBORuntimeData(String propertyName, FloatBuffer newData) implements IResourceRuntimeData {
}
