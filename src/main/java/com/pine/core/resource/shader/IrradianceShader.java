package com.pine.core.resource.shader;


public class IrradianceShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag");
    }
}