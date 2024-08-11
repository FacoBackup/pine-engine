package com.jengine.jengine.app.engine.resource;

import com.jengine.jengine.IResource;
import com.jengine.jengine.ResourceRuntimeException;
import org.springframework.stereotype.Component;

@Component
public interface IShader extends IResource {
    void compile() throws ResourceRuntimeException;
}
