package com.pine.tools;

import com.pine.annotation.EngineExternalModule;
import com.pine.service.system.AbstractSystem;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.system.GridSystem;

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
