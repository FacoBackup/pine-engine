package com.pine.engine.core.modules;

import com.pine.common.Updatable;
import com.pine.engine.core.system.ISystem;

import java.util.List;

public interface IExternalService extends Updatable {
    /**
     * Should return a list containing the previous registered systems and the new one included
     * @param systems
     * @return
     */
    List<ISystem> externalSystems(List<ISystem> systems);
}
