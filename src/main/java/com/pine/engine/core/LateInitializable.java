package com.pine.engine.core;

public interface LateInitializable {
    /**
     * Executed after every EngineInjectable has already been initialized
     */
    void lateInitialize();
}
