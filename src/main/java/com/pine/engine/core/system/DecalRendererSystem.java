package com.pine.engine.core.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.pine.engine.Engine;


@All
public class DecalRendererSystem extends IteratingSystem implements ISystem {
    private Engine engine;

    @Override
    public void setEngine (Engine engine){
        this.engine = engine;
    }

    @Override
    protected void process(int id) {
    }
}
