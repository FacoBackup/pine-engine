package com.jengine.jengine.app.engine.resource.shader;

import com.jengine.jengine.ResourceRuntimeException;
import com.jengine.jengine.app.engine.resource.AbstractShader;
import com.jengine.jengine.app.engine.resource.IShader;


public class VisibilityShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag");
    }
}






