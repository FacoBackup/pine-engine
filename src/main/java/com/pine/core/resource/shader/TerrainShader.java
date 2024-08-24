package com.pine.core.resource.shader;

import com.pine.app.ResourceRuntimeException;


public class TerrainShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/TERRAIN.vert", "shaders/TERRAIN.frag");
    }
}