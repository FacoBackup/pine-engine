package com.pine.core.service.system.impl;

import com.pine.core.EngineDependency;
import com.pine.core.repository.CoreResourceRepository;
import com.pine.core.service.resource.UBOService;
import com.pine.core.service.system.AbstractSystem;

import static com.pine.Engine.MAX_LIGHTS;

public class UBOSyncSystem extends AbstractSystem {
    @EngineDependency
    public UBOService uboService;

    @EngineDependency
    public CoreResourceRepository resources;

    @Override
    protected void renderInternal() {
        uboService.updateBuffer(resources.cameraViewUBO, resources.cameraViewUBOState, 0);
        uboService.updateBuffer(resources.frameCompositionUBO, resources.frameCompositionUBOState, 0);
        uboService.updateBuffer(resources.lensPostProcessingUBO, resources.lensPostProcessingUBOState, 0);
        uboService.updateBuffer(resources.ssaoUBO, resources.ssaoUBOState, 0);
        uboService.updateBuffer(resources.uberUBO, resources.uberUBOState, 0);
        uboService.updateBuffer(resources.lightsUBO, resources.lightsUBOState, 0);
        uboService.updateBuffer(resources.lightsUBO, resources.lightsUBOState2, MAX_LIGHTS * 16);
        uboService.updateBuffer(resources.cameraProjectionUBO, resources.cameraProjectionUBOState, 0);
    }
}
