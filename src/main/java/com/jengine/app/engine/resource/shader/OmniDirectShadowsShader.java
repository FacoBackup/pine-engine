package com.jengine.app.engine.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class OmniDirectShadowsShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag");
    }
}