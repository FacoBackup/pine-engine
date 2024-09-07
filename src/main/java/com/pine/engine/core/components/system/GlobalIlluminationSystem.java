package com.pine.engine.core.components.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.pine.engine.Engine;


@All
public class GlobalIlluminationSystem extends IteratingSystem implements ISystem {
    private Engine engine;

    @Override
    public void setEngine (Engine engine){
        this.engine = engine;
    }

    @Override
    protected void process(int id) {
        doAo();
        doGi();
    }

    private void doGi(){

    }

    private void doAo(){

    }
}
