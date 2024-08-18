package com.jengine.app.engine.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class DirectShadowsShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag");
    }
}

