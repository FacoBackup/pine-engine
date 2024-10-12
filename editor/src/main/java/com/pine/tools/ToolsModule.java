package com.pine.tools;

import com.pine.injection.EngineExternalModule;
import com.pine.service.system.AbstractPass;
import com.pine.service.system.impl.GBufferPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.system.GridPass;

import java.util.ArrayList;
import java.util.List;

public class ToolsModule implements EngineExternalModule {

    @Override
    public List<AbstractPass> getExternalSystems(List<AbstractPass> systems) {
        ArrayList<AbstractPass> withTools = new ArrayList<>(systems);
        AbstractPass fc = systems.stream().filter(a -> a instanceof GBufferPass).findFirst().orElse(null);
        int indexFc = systems.indexOf(fc);
        withTools.add(indexFc, new GridPass());
//        withTools.add(new CullingVisualizationSystem());
        return withTools;
    }

    @Override
    public List<Object> getInjectables() {
        return List.of(new ToolsResourceRepository());
    }
}
