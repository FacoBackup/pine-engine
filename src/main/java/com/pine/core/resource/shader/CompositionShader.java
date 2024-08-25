package com.pine.core.resource.shader;


public class CompositionShader extends AbstractShader {
    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag");
    }
}
