package com.jengine.app.engine.resource.shader;


import com.jengine.app.ResourceRuntimeException;


public class IrradianceShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag");
    }
}