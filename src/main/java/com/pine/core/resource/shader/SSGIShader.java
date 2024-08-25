package com.pine.core.resource.shader;


public class SSGIShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/SSGI.frag");
    }
}