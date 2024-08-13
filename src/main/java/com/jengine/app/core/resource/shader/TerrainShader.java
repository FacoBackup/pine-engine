package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.core.resource.AbstractShader;
import com.jengine.app.core.resource.IShader;


public class TerrainShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/TERRAIN.vert", "shaders/TERRAIN.frag");
    }
}