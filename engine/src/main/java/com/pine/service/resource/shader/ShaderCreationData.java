package com.pine.service.resource.shader;

import com.pine.service.resource.resource.ResourceCreationData;
import com.pine.service.resource.resource.ResourceType;

public final class ShaderCreationData extends ResourceCreationData {
    public static final String LOCAL_SHADER = "shaders/";
    private final String vertex;
    private final String fragment;
    private final boolean localResource;

    public ShaderCreationData(String vertex, String fragment) {
        this.vertex = vertex;
        this.fragment = fragment;
        this.localResource = vertex.contains(LOCAL_SHADER);
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

    public boolean isLocalResource() {
        return localResource;
    }
}
