package com.pine.engine.core.service;

import com.pine.common.Initializable;
import com.pine.engine.Engine;

public interface EngineInjectable extends Initializable {
    default void setEngine(Engine engine) {
    }

    @Override
    default void onInitialize() {
    }
}
