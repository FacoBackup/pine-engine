package com.pine.engine.core.service.system.impl;

import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.repository.CoreResourceRepository;
import com.pine.engine.core.service.resource.UBOService;
import com.pine.engine.core.service.system.AbstractSystem;

import static com.pine.engine.Engine.MAX_LIGHTS;

public class UBOSyncSystem extends AbstractSystem {
    @EngineDependency
    public UBOService uboService;

    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

    @Override
    protected void renderInternal() {
        uboService.bind(coreResourceRepository.cameraViewUBO);
        uboService.updateBuffer(coreResourceRepository.cameraViewUBOState, 0);

        uboService.bind(coreResourceRepository.frameCompositionUBO);
        uboService.updateBuffer(coreResourceRepository.frameCompositionUBOState, 0);

        uboService.bind(coreResourceRepository.lensPostProcessingUBO);
        uboService.updateBuffer(coreResourceRepository.lensPostProcessingUBOState, 0);

        uboService.bind(coreResourceRepository.ssaoUBO);
        uboService.updateBuffer(coreResourceRepository.ssaoUBOState, 0);

        uboService.bind(coreResourceRepository.uberUBO);
        uboService.updateBuffer(coreResourceRepository.uberUBOState, 0);

        uboService.bind(coreResourceRepository.lightsUBO);
        uboService.updateBuffer(coreResourceRepository.lightsUBOState, 0);
        uboService.bind(coreResourceRepository.lightsUBO);
        uboService.updateBuffer(coreResourceRepository.lightsUBOState2, MAX_LIGHTS * 16);

        uboService.bind(coreResourceRepository.cameraProjectionUBO);
        uboService.updateBuffer(coreResourceRepository.cameraProjectionUBOState, 0);
    }
}
