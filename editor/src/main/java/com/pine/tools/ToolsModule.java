package com.pine.tools;

import com.pine.injection.EngineExternalModule;
import com.pine.service.system.AbstractPass;
import com.pine.service.system.impl.*;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.system.GridPass;
import com.pine.tools.system.IconsPass;
import com.pine.tools.system.PaintGizmoPass;
import com.pine.tools.system.PaintGizmoRenderingPass;
import com.pine.tools.system.outline.BoxOutlineGenPass;
import com.pine.tools.system.outline.OutlineGenPass;
import com.pine.tools.system.outline.OutlineRenderingPass;

import java.util.List;

public class ToolsModule implements EngineExternalModule {

    @Override
    public AbstractPass[] getExternalSystems() {
        return new AbstractPass[]{
                new NoiseGenPass(),
                new BRDFGenPass(),
                new IrradianceGenPass(),
                new EnvironmentMapFilteringGenPass(),
                new ShaderDataSyncPass(),
                new FoliageCullingPass(),
                new TerrainGBufferPass(),
                new PaintGizmoPass(),
                new PrimitiveGBufferPass(),
                new FoliageGBufferPass(),
                new CopyDepthPass(),
                new DecalGBufferPass(),
                new IconsPass(),
                new GBufferShadingPass(),
                new AtmospherePass(),
                new CompositingPass(),
                new PostProcessingPass(),
                new GridPass(),
                new OutlineGenPass(),
                new BoxOutlineGenPass(),
                new OutlineRenderingPass(),
                new PaintGizmoRenderingPass(),
                new VoxelVisualizerPass(),
                new FrameCompositionPass()
        };
    }

    @Override
    public List<Object> getInjectables() {
        return List.of(new ToolsResourceRepository());
    }
}
