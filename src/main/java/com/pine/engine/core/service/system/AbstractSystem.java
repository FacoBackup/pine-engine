package com.pine.engine.core.service.system;


import com.pine.common.Updatable;
import com.pine.common.Initializable;
import com.pine.common.Renderable;
import com.pine.engine.Engine;
import com.pine.engine.core.EngineDependency;

public abstract class AbstractSystem implements Initializable {

    @EngineDependency
    public Engine engine;

    /**
     * Logic and state update
     */
    public abstract void render();

    @Override
    public void onInitialize() {
    }
}
