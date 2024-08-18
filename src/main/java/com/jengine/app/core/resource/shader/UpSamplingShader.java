package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class UpSamplingShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl");
    }
}
