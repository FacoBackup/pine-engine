package com.jengine.jengine.app.engine.resource;

import com.jengine.jengine.Loggable;
import com.jengine.jengine.ResourceRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShaderRepository implements Loggable {

    @Autowired
    private List<IShader> shaders;

    public void compileAll() throws ResourceRuntimeException {
        for (IShader shader : shaders) {
            try {
                shader.compile();
            } catch (ResourceRuntimeException ex) {
                getLogger().error("Error compiling shader {}", shader.getClass().getSimpleName(), ex);
                throw ex;
            }
        }
    }
}
