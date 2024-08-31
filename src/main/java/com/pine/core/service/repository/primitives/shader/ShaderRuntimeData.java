package com.pine.core.service.repository.primitives.shader;

import com.pine.core.service.common.IResourceRuntimeData;

import java.util.HashMap;
import java.util.Map;

public class ShaderRuntimeData implements IResourceRuntimeData {
    private final Map<String, Object> uniformData = new HashMap<>();

    public Map<String, Object> getUniformData() {
        return uniformData;
    }
}
