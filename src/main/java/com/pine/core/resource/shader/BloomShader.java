package com.pine.core.resource.shader;

import com.pine.app.ResourceRuntimeException;


public class BloomShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag");
    }
}
