package com.pine.core.resource.shader;

import com.pine.app.ResourceRuntimeException;


public class DirectShadowsShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag");
    }
}

