package com.pine.core.resource.shader;


public class ToScreenShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/TO_SCREEN.frag");
    }
}