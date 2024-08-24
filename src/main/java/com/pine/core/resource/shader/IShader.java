package com.pine.core.resource.shader;

import com.pine.app.IResource;
import com.pine.app.ResourceRuntimeException;
import org.springframework.stereotype.Component;

public interface IShader extends IResource {
    void compile() throws ResourceRuntimeException;
}
