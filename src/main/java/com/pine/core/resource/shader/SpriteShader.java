package com.pine.core.resource.shader;

import com.pine.app.ResourceRuntimeException;


public class SpriteShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/SPRITE.vert", "shaders/SPRITE.frag");
    }
}