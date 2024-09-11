package com.pine.engine.tools;

import com.pine.engine.core.modules.EngineExternalModule;
import com.pine.engine.core.service.EngineInjectable;
import com.pine.engine.core.system.ISystem;
import com.pine.engine.tools.repository.ToolsResourceRepository;
import com.pine.engine.tools.system.GridSystem;

import java.util.ArrayList;
import java.util.List;

public class ToolsModule implements EngineExternalModule {

    @Override
    public List<ISystem> getExternalSystems(List<ISystem> systems) {
        ArrayList<ISystem> withTools = new ArrayList<>(systems);
        withTools.add(new GridSystem());
        return withTools;
    }

    @Override
    public List<? extends EngineInjectable> getInjectables() {
        return List.of(new ToolsResourceRepository());
    }
}
