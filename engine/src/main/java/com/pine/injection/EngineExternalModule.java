package com.pine.injection;

import com.pine.service.system.AbstractPass;

import java.util.Collections;
import java.util.List;

public interface EngineExternalModule {
    /**
     * Should return a list containing the previous registered systems and the new ones included
     *
     * @param systems: currently instantiated systems
     * @return instantiated systems + additional systems
     */
    default List<AbstractPass> getExternalSystems(List<AbstractPass> systems) {
        return systems;
    }

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
