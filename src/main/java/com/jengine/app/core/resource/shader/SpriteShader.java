package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;


public class SpriteShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/SPRITE.vert", "shaders/SPRITE.frag");
    }
}