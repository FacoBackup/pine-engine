package com.pine.core.resource.shader;

public class AtmosphereShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/ATMOSPHERE.frag");
    }
}
