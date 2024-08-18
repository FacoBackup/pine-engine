package com.jengine.app.engine.resource.shader;

import com.jengine.app.IResource;
import com.jengine.app.ResourceRuntimeException;
import org.springframework.stereotype.Component;

public interface IShader extends IResource {
    void compile() throws ResourceRuntimeException;
}
