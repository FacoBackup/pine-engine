package com.pine.engine.components.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.pine.engine.Engine;


@All
public class PreRendererSystem extends IteratingSystem implements ISystem {
    private Engine engine;

    @Override
    public void setEngine (Engine engine){
        this.engine = engine;
    }

    @Override
    protected void process(int id) {
    }
}
