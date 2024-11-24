package com.pine.editor.tools;

import com.pine.editor.tools.system.GridPass;
import com.pine.editor.tools.system.IconsPass;
import com.pine.editor.tools.system.PaintGizmoPass;
import com.pine.engine.injection.EngineExternalModule;
import com.pine.engine.service.system.AbstractPass;
import com.pine.engine.service.system.impl.*;
import com.pine.engine.service.system.impl.gbuffer.*;
import com.pine.editor.tools.repository.ToolsResourceRepository;
import com.pine.editor.tools.system.PaintGizmoRenderingPass;
import com.pine.editor.tools.system.outline.BoxOutlineGenPass;
import com.pine.editor.tools.system.outline.OutlineGenPass;
import com.pine.editor.tools.system.outline.OutlineRenderingPass;

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
                new TerrainGBufferPass(),
                new PaintGizmoPass(),
                new FoliageCullingPass(),
                new FoliageGBufferPass(),
                new PrimitiveGBufferPass(),
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
