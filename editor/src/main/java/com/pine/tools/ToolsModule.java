package com.pine.tools;

import com.pine.injection.EngineExternalModule;
import com.pine.service.system.AbstractPass;
import com.pine.service.system.impl.*;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.system.*;
import com.pine.tools.system.outline.BoxOutlineGenPass;
import com.pine.tools.system.outline.OutlineGenPass;
import com.pine.tools.system.outline.OutlineRenderingPass;
import com.pine.tools.system.outline.TerrainOutlineGenPass;

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
                new OutlineGenPass(),
                new TerrainOutlineGenPass(),
                new BoxOutlineGenPass(),
                new OutlineRenderingPass(),
                new PaintGizmoRenderingPass(),
                new VoxelVisualizerPass(),
                new GridPass(),
                new FrameCompositionPass()
        };
    }

    @Override
    public List<Object> getInjectables() {
        return List.of(new ToolsResourceRepository());
    }
}
