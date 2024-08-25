package com.pine.core.resource.shader;


public class DirectShadowsShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag");
    }
}

