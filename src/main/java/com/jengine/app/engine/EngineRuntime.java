package com.jengine.app.engine;

import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.engine.repository.MeshRepository;
import com.jengine.app.engine.repository.ShaderRepository;
import com.jengine.app.engine.repository.WorldRepository;
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

    public void init() throws ResourceRuntimeException {
        shaders.compileAll();
        meshes.loadAll();
    }

    public void render() {

    }
}
