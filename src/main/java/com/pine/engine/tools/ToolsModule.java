package com.pine.engine.tools;

import com.pine.engine.core.modules.EngineExternalModule;
import com.pine.engine.core.system.AbstractSystem;
import com.pine.engine.tools.repository.ToolsResourceRepository;
import com.pine.engine.tools.system.GridSystem;

import java.util.ArrayList;
import java.util.List;

public class ToolsModule implements EngineExternalModule {

    @Override
    public List<AbstractSystem> getExternalSystems(List<AbstractSystem> systems) {
        ArrayList<AbstractSystem> withTools = new ArrayList<>(systems);
        withTools.add(new GridSystem());
        return withTools;
    }

    @Override
    public List<Object> getInjectables() {
        return List.of(new ToolsResourceRepository());
    }
}
