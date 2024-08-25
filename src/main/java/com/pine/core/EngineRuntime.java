package com.pine.core;

import com.pine.core.repository.MeshRepository;
import com.pine.core.repository.ShaderRepository;
import com.pine.core.repository.WorldRepository;
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

    public void init() throws RuntimeException {

    }

    public void render() {

    }
}
