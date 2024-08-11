package com.jengine.jengine.app.engine.resource.shader;

import com.jengine.jengine.ResourceRuntimeException;
import com.jengine.jengine.app.engine.resource.AbstractShader;
import com.jengine.jengine.app.engine.resource.IShader;


public class BokehShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/BOKEH.frag");
    }
}