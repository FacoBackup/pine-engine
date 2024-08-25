package com.pine.core.resource.shader;


public class BoxBlurShader extends AbstractShader  {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/BOX-BLUR.frag");
    }
}