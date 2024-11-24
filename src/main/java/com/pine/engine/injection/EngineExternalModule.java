package com.pine.engine.injection;

import com.pine.engine.service.system.AbstractPass;

import java.util.Collections;
import java.util.List;

public interface EngineExternalModule {
    AbstractPass[] getExternalSystems();

    default void onInitialize() {
    }

    /**
     * Only accessible through systems via @EngineDependency
     * @return
     */
    default List<Object> getInjectables() {
        return Collections.emptyList();
    }
}
