package com.jengine.jengine.app.engine;

import com.jengine.jengine.app.engine.resource.WorldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EngineRuntime {
    private long elapsedTime = 0;

    @Autowired
    private WorldRepository world;

    public void render() {

    }
}
