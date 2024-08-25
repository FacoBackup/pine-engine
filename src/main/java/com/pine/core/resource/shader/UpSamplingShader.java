package com.pine.core.resource.shader;


public class UpSamplingShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl");
    }
}
