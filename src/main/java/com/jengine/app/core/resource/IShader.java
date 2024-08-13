package com.jengine.app.core.resource;

import com.jengine.app.IResource;
import com.jengine.app.ResourceRuntimeException;
import org.springframework.stereotype.Component;

@Component
public interface IShader extends IResource {
    void compile() throws ResourceRuntimeException;
}
