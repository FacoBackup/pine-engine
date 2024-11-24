package com.pine.service.resource.shader;

import com.pine.service.resource.IResourceCreationData;

public class ShaderCreationData implements IResourceCreationData {
    public final String computePath;
    public final String vertexPath;
    public final String fragmentPath;

    public ShaderCreationData(String vertexPath, String fragmentPath) {
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;
        computePath = null;
    }

    public ShaderCreationData(String computePath) {
        this.vertexPath = null;
        this.fragmentPath = null;
        this.computePath = computePath;
    }
}
