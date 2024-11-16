package com.pine.tools;

import com.pine.injection.EngineExternalModule;
import com.pine.service.system.AbstractPass;
import com.pine.service.system.impl.FrameCompositionPass;
import com.pine.service.system.impl.GBufferShadingPass;
import com.pine.service.system.impl.TerrainGBufferPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.system.*;

import java.util.ArrayList;
import java.util.List;

public class ToolsModule implements EngineExternalModule {

    @Override
    public List<AbstractPass> getExternalSystems(List<AbstractPass> systems) {
        ArrayList<AbstractPass> withTools = new ArrayList<>(systems);
        AbstractPass fc = systems.stream().filter(a -> a instanceof FrameCompositionPass).findFirst().orElse(null);
        AbstractPass tB = systems.stream().filter(a -> a instanceof TerrainGBufferPass).findFirst().orElse(null);
        AbstractPass s = systems.stream().filter(a -> a instanceof GBufferShadingPass).findFirst().orElse(null);
        int indexFc = systems.indexOf(fc);
        int indexTB = systems.indexOf(tB);

        withTools.add(indexFc, new GridPass());
        withTools.add(indexTB + 1, new PaintGizmoPass());
        withTools.add(indexFc, new PaintGizmoRenderingPass());
        withTools.add(indexFc, new OutlinePass());
        withTools.add(indexFc, new OutlineGenPass());

        int shadingIndex = systems.indexOf(s);
        withTools.add(shadingIndex + 1, new IconsPass());
        return withTools;
    }

    @Override
    public List<Object> getInjectables() {
        return List.of(new ToolsResourceRepository());
    }
}
