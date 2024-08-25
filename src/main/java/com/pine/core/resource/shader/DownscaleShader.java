package com.pine.core.resource.shader;


public class DownscaleShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl");
    }
}
