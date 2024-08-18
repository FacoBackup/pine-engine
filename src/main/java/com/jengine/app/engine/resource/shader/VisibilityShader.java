package com.jengine.app.engine.resource.shader;

import com.jengine.app.ResourceRuntimeException;
import org.springframework.stereotype.Component;

@Component
public class VisibilityShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag");
    }
}






