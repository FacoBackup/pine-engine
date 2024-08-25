package com.pine.core.resource.shader;


public class BilateralBlurShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl");
    }
}