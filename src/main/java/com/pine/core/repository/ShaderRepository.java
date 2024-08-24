package com.pine.core.repository;

import com.pine.app.Loggable;
import com.pine.app.ResourceRuntimeException;
import com.pine.core.resource.shader.IShader;
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
                getLogger().error("Error compiling shader {}", shader.getClass().getName(), ex);
                throw ex;
            }
        }
    }
}
