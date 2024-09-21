package com.pine.injection;

public interface LateInitializable {
    /**
     * Executed after every EngineInjectable has already been initialized
     */
    void lateInitialize();
}
