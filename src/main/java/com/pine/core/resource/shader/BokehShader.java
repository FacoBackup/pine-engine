package com.pine.core.resource.shader;

import com.pine.app.ResourceRuntimeException;


public class BokehShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/BOKEH.frag");
    }
}