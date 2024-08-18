package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class OmniDirectShadowsShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag");
    }
}