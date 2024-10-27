package com.pine.service.resource.shader;

import com.pine.service.resource.LocalResourceType;
import com.pine.service.resource.ResourceCreationData;

public final class ShaderCreationData extends ResourceCreationData {
    public static final String LOCAL_SHADER = "shaders/";
    private String vertex;
    private String fragment;
    private String compute;
    private final boolean localResource;

    public ShaderCreationData(String vertex, String fragment) {
        this.vertex = vertex;
        this.fragment = fragment;
        this.localResource = vertex.contains(LOCAL_SHADER);
    }

    public ShaderCreationData(String compute) {
        this.compute = compute;
        this.localResource = compute.contains(LOCAL_SHADER);
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.SHADER;
    }

    public String getCompute() {
        return compute;
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
