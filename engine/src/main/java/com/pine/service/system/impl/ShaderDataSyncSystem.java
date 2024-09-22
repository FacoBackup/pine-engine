package com.pine.service.system.impl;

import com.pine.Loggable;
import com.pine.PInject;
import com.pine.repository.CoreResourceRepository;
import com.pine.repository.RenderingRepository;
import com.pine.service.resource.ComputeService;
import com.pine.service.resource.SSBOService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.UBOService;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static com.pine.Engine.MAX_LIGHTS;

public class ShaderDataSyncSystem extends AbstractSystem implements Loggable {
    @PInject
    public UBOService uboService;

    @PInject
    public SSBOService ssboService;

    @PInject
    public CoreResourceRepository resources;

    @PInject
    public ComputeService computeService;

    @PInject
    public RenderingRepository renderingRepository;

    private UniformDTO entityCount;
    private final IntBuffer entityCountBuffer = MemoryUtil.memAllocInt(1);

    @Override
    public void onInitialize() {
       entityCount = resources.transformationCompute.addUniformDeclaration("entityCount", GLSLType.INT);
    }

    @Override
    protected void renderInternal() {
        updateUBOs();
        ssboService.updateBuffer(resources.transformationSSBO, resources.transformationSSBOState, 0);

        ssboService.bind(resources.transformationSSBO);
        ssboService.bind(resources.modelSSBO);
        computeService.bind(resources.transformationCompute);
        entityCountBuffer.put(0, renderingRepository.requestCount);
        computeService.bindUniform(entityCount, entityCountBuffer);
        computeService.compute();
        ssboService.unbind();
    }

    private void updateUBOs() {
        uboService.updateBuffer(resources.cameraViewUBO, resources.cameraViewUBOState, 0);
        uboService.updateBuffer(resources.cameraProjectionUBO, resources.cameraProjectionUBOState, 0);
        uboService.updateBuffer(resources.frameCompositionUBO, resources.frameCompositionUBOState, 0);
        uboService.updateBuffer(resources.lensPostProcessingUBO, resources.lensPostProcessingUBOState, 0);
        uboService.updateBuffer(resources.ssaoUBO, resources.ssaoUBOState, 0);
        uboService.updateBuffer(resources.uberUBO, resources.uberUBOState, 0);
        uboService.updateBuffer(resources.lightsUBO, resources.lightsUBOState, 0);
        uboService.updateBuffer(resources.lightsUBO, resources.lightsUBOState2, MAX_LIGHTS * 16);
    }
}
