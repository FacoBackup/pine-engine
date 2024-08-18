package com.jengine.app.engine.repository;

import com.jengine.app.Loggable;
import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.engine.resource.shader.IShader;
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
