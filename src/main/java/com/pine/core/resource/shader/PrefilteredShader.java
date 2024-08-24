package com.pine.core.resource.shader;

import com.pine.app.ResourceRuntimeException;


public class PrefilteredShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag");
    }
}