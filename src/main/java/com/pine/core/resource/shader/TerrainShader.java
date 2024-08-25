package com.pine.core.resource.shader;


public class TerrainShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/TERRAIN.vert", "shaders/TERRAIN.frag");
    }
}