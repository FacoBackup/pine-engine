package com.jengine.app.engine.resource.shader;


import com.jengine.app.ResourceRuntimeException;


public class MotionBlurShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag");
    }
}
