package com.pine.engine.core.system;


import com.pine.engine.Engine;

public abstract class AbstractSystem implements ISystem {

    @InjectEngineDependency
    public Engine engine;
}
