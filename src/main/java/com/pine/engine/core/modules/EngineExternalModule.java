package com.pine.engine.core.modules;

import com.pine.common.Initializable;
import com.pine.engine.core.service.EngineInjectable;
import com.pine.engine.core.system.ISystem;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface EngineExternalModule extends Initializable {
    /**
     * Should return a list containing the previous registered systems and the new ones included
     *
     * @param systems: currently instantiated systems
     * @return instantiated systems + additional systems
     */
    default List<ISystem> getExternalSystems(List<ISystem> systems) {
        return systems;
    }

    @Override
    default void onInitialize() {
    }

    /**
     * Only accessible through systems via @InjectEngineDependency
     * @return
     */
    default List<? extends EngineInjectable> getInjectables() {
        return Collections.emptyList();
    }
}
