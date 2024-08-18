package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class BokehShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/BOKEH.frag");
    }
}