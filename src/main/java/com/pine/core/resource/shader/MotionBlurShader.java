package com.pine.core.resource.shader;


public class MotionBlurShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag");
    }
}
