package com.pine.core.resource.shader;


import com.pine.app.ResourceRuntimeException;


public class MotionBlurShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag");
    }
}
