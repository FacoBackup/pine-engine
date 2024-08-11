package com.jengine.jengine.app.engine;

import com.jengine.jengine.ResourceRuntimeException;
import com.jengine.jengine.app.engine.resource.MeshRepository;
import com.jengine.jengine.app.engine.resource.ShaderRepository;
import com.jengine.jengine.app.engine.resource.WorldRepository;
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
