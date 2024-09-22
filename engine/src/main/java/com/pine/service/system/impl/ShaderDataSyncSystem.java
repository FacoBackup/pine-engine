package com.pine.service.system.impl;

import com.pine.PInject;
import com.pine.repository.CoreResourceRepository;
import com.pine.service.resource.ComputeService;
import com.pine.service.resource.SSBOService;
import com.pine.service.resource.UBOService;
import com.pine.service.system.AbstractSystem;

import static com.pine.Engine.MAX_LIGHTS;

public class ShaderDataSyncSystem extends AbstractSystem {
    @PInject
    public UBOService uboService;

    @PInject
    public SSBOService ssboService;

    @PInject
    public CoreResourceRepository resources;

    @PInject
    public ComputeService computeService;

    @Override
    protected void renderInternal() {

        ssboService.updateBuffer(resources.transformationSSBO, resources.transformationSSBOState, 0);

        computeService.bind(resources.transformationCompute);

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
