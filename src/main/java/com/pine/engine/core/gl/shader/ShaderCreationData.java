package com.pine.engine.core.gl.shader;

import com.pine.engine.core.service.resource.resource.ResourceCreationData;
import com.pine.engine.core.service.resource.resource.ResourceType;

public final class ShaderCreationData extends ResourceCreationData {
    private final String vertex;
    private final String fragment;
    private final String absoluteId;

    public ShaderCreationData(String vertex, String fragment, String absoluteId) {
        this.vertex = vertex;
        this.fragment = fragment;
        this.absoluteId = absoluteId;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }

    public String vertex() {
        return vertex;
    }

    public String fragment() {
        return fragment;
    }

    public String absoluteId() {
        return absoluteId;
    }

}
