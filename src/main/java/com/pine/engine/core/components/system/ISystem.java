package com.pine.engine.core.components.system;

import com.pine.engine.Engine;

public interface ISystem {
    /**
     * Logic and state update
     */
    default void tick(){
    }

    /**
     * Actual rendering
     */
    void process();

    void setEngine(Engine engine);
}
