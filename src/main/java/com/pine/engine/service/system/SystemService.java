package com.pine.engine.service.system;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.injection.PInjector;
import com.pine.engine.service.system.impl.*;
import com.pine.engine.service.system.impl.gbuffer.*;
import com.pine.engine.service.module.Initializable;
import com.pine.engine.tasks.SyncTask;

@PBean
public class SystemService implements SyncTask, Initializable {
    @PInject
    public PInjector pInjector;

    private AbstractPass[] systems = new AbstractPass[]{
            new NoiseGenPass(),
            new BRDFGenPass(),
            new IrradianceGenPass(),
            new EnvironmentMapFilteringGenPass(),
            new ShaderDataSyncPass(),
            new FoliageCullingPass(),
            new TerrainGBufferPass(),
            new PrimitiveGBufferPass(),
            new FoliageGBufferPass(),
            new CopyDepthPass(),
            new DecalGBufferPass(),
            new GBufferShadingPass(),
            new AtmospherePass(),
            new CompositingPass(),
            new PostProcessingPass(),
            new VoxelVisualizerPass(),
            new FrameCompositionPass()
    };

    public void setSystems(AbstractPass[] systems) {
        for (var sys : systems) {
            pInjector.inject(sys);
            sys.onInitialize();
        }
        this.systems = systems;
    }

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