package com.jengine.app.engine.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class BloomShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag");
    }
}
