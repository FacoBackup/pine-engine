package com.jengine.jengine.app.engine.resource.shader;

import com.jengine.jengine.ResourceRuntimeException;
import com.jengine.jengine.app.engine.resource.AbstractShader;
import com.jengine.jengine.app.engine.resource.IShader;


public class TerrainShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/TERRAIN.vert", "shaders/TERRAIN.frag");
    }
}