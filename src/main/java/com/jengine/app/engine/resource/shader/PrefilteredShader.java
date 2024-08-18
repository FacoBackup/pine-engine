package com.jengine.app.engine.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class PrefilteredShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag");
    }
}