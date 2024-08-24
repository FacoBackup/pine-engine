package com.pine.core.resource.shader;

import com.pine.app.ResourceRuntimeException;


public class BilateralBlurShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl");
    }
}