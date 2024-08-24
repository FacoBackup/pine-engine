package com.pine.core.resource.shader;


import com.pine.app.ResourceRuntimeException;


public class IrradianceShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag");
    }
}