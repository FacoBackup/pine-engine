package com.pine.engine.core.system;


import com.pine.common.Updatable;
import com.pine.common.Initializable;
import com.pine.common.Renderable;
import com.pine.engine.Engine;
import com.pine.engine.core.EngineDependency;

public abstract class AbstractSystem implements Initializable, Renderable, Updatable {

    @EngineDependency
    public Engine engine;

    /**
     * Logic and state update
     */
    @Override
    public void tick() {
    }

    @Override
    public void render() {
    }

    @Override
    public void onInitialize() {
    }
}
