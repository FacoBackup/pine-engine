package com.pine.core.resource.shader;


public class PrefilteredShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag");
    }
}