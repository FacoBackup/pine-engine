package com.pine.core.resource.shader;

import org.springframework.stereotype.Component;

@Component
public class VisibilityShader extends AbstractShader {

    @Override
    public void compile() throws RuntimeException {
        compile("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag");
    }
}






