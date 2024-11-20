package com.pine.injection;

import com.pine.service.system.AbstractPass;

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
