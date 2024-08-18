package com.jengine.app.core.resource.shader;

import com.jengine.app.IResource;
import com.jengine.app.ResourceRuntimeException;
import org.springframework.stereotype.Component;

public interface IShader extends IResource {
    void compile() throws ResourceRuntimeException;
}
