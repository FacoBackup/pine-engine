package com.jengine.app.core;

import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.core.repository.MeshRepository;
import com.jengine.app.core.repository.ShaderRepository;
import com.jengine.app.core.repository.WorldRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EngineRuntime {
    private long elapsedTime = 0;

    @Autowired
    private WorldRepository world;

    @Autowired
    private ShaderRepository shaders;

    @Autowired
    private MeshRepository meshes;

    @PostConstruct
    private void init() throws ResourceRuntimeException {
        shaders.compileAll();
        meshes.loadAll();
    }

    public void render() {

    }
}
