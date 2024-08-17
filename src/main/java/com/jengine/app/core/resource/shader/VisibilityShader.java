package com.jengine.app.core.resource.shader;

import com.jengine.app.ResourceRuntimeException;
import org.springframework.stereotype.Component;

@Component
public class VisibilityShader extends AbstractShader implements IShader {
    public void compile() throws ResourceRuntimeException {
        compile("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag");
    }
}






