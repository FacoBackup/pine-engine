package com.pine.engine.service.system;

import com.pine.common.Initializable;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.injection.PInjector;
import com.pine.engine.service.system.impl.*;
import com.pine.engine.service.system.impl.gbuffer.*;
import com.pine.engine.service.system.impl.tools.GridPass;
import com.pine.engine.service.system.impl.tools.IconsPass;
import com.pine.engine.service.system.impl.tools.PaintGizmoPass;
import com.pine.engine.service.system.impl.tools.PaintGizmoRenderingPass;
import com.pine.engine.service.system.impl.tools.outline.BoxOutlineGenPass;
import com.pine.engine.service.system.impl.tools.outline.OutlineGenPass;
import com.pine.engine.service.system.impl.tools.outline.OutlineRenderingPass;
import com.pine.engine.tasks.SyncTask;

@PBean
public class SystemService implements SyncTask, Initializable {
    @PInject
    public PInjector pInjector;

    private final AbstractPass[] systems = new AbstractPass[]{
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

    @Override
    public void sync() {
        for (var system : systems) {
            system.render();
        }
    }

    @Override
    public void onInitialize() {
        for (var sys : systems) {
            pInjector.inject(sys);
            sys.onInitialize();
        }
    }
}