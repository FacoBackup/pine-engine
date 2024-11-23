package com.pine.service.system;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;
import com.pine.service.system.impl.*;
import com.pine.service.system.impl.gbuffer.*;
import com.pine.tasks.SyncTask;

@PBean
public class SystemService implements SyncTask {
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


    public void initialize() {
        for (var sys : systems) {
            pInjector.inject(sys);
            sys.onInitialize();
        }
    }
}