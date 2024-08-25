package com.pine.core.resource.shader;


public class OmniDirectShadowsShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag");
    }
}