package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class SsgiShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/QUAD.vert", "shaders/SSGI.frag");
    }
}