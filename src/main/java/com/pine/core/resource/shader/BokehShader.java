package com.pine.core.resource.shader;


public class BokehShader extends AbstractShader {

    public void compile() throws RuntimeException {
        compile("shaders/QUAD.vert", "shaders/BOKEH.frag");
    }
}