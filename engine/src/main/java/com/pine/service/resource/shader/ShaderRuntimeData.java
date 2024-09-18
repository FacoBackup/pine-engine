package com.pine.service.resource.shader;

import com.pine.service.resource.resource.IResourceRuntimeData;

import java.util.HashMap;
import java.util.Map;

public class ShaderRuntimeData implements IResourceRuntimeData {
    private final Map<String, Object> uniformData = new HashMap<>();

    public Map<String, Object> getUniformData() {
        return uniformData;
    }
}
