package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class DownscaleShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl");
    }
}
