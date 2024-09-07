package com.pine.engine.service.primitives.shader;

import com.pine.engine.resource.IResourceRuntimeData;

import java.util.HashMap;
import java.util.Map;

public class ShaderRuntimeData implements IResourceRuntimeData {
    private final Map<String, Object> uniformData = new HashMap<>();

    public Map<String, Object> getUniformData() {
        return uniformData;
    }
}
