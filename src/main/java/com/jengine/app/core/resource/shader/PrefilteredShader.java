package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class PrefilteredShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag");
    }
}