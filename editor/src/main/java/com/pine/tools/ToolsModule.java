package com.pine.tools;

import com.pine.injection.EngineExternalModule;
import com.pine.service.system.AbstractSystem;
import com.pine.service.system.impl.FrameCompositionSystem;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.system.CullingVisualizationSystem;
import com.pine.tools.system.DebugSystem;
import com.pine.tools.system.GridSystem;

import java.util.ArrayList;
import java.util.List;

public class ToolsModule implements EngineExternalModule {

    @Override
    public List<AbstractSystem> getExternalSystems(List<AbstractSystem> systems) {
        ArrayList<AbstractSystem> withTools = new ArrayList<>(systems);
        AbstractSystem fc = systems.stream().filter(a -> a instanceof FrameCompositionSystem).findFirst().orElse(null);
        int indexFc = systems.indexOf(fc);
        withTools.add(indexFc, new DebugSystem());
        withTools.add(new GridSystem());
        withTools.add(new CullingVisualizationSystem());
        return withTools;
    }

    @Override
    public List<Object> getInjectables() {
        return List.of(new ToolsResourceRepository());
    }
}
