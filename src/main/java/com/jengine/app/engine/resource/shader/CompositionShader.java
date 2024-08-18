package com.jengine.app.engine.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class CompositionShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag");
    }
}
