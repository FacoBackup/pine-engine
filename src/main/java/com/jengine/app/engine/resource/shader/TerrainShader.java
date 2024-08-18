package com.jengine.app.engine.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class TerrainShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/TERRAIN.vert", "shaders/TERRAIN.frag");
    }
}