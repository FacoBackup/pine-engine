package com.jengine.app.core.resource.shader;


import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.core.resource.AbstractShader;
import com.jengine.app.core.resource.IShader;


public class IrradianceShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag");
    }
}