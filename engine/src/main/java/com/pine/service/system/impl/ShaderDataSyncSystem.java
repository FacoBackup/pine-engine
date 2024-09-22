package com.pine.service.system.impl;

import com.pine.Loggable;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static com.pine.Engine.MAX_LIGHTS;

public class ShaderDataSyncSystem extends AbstractSystem implements Loggable {

    private UniformDTO entityCount;
    private final IntBuffer countBuffer = MemoryUtil.memAllocInt(1);

    @Override
    public void onInitialize() {
       entityCount = computeRepository.transformationCompute.addUniformDeclaration("entityCount", GLSLType.INT);
    }

    @Override
    protected void renderInternal() {
        updateUBOs();
        ssboService.updateBuffer(ssboRepository.transformationSSBO, ssboRepository.transformationSSBOState, 0);

        if(renderingRepository.needsLightUpdate){
            ssboService.updateBuffer(ssboRepository.lightDescriptionSSBO, ssboRepository.lightSSBOState, MAX_LIGHTS * 16);
            renderingRepository.needsLightUpdate = false;
        }

        ssboService.bind(ssboRepository.lightDescriptionSSBO);
        ssboService.bind(ssboRepository.transformationSSBO);
        ssboService.bind(ssboRepository.modelSSBO);

        computeService.bind(computeRepository.transformationCompute);
        countBuffer.put(0, renderingRepository.requestCount);
        computeService.bindUniform(entityCount, countBuffer);
        computeService.compute();
        ssboService.unbind();
    }

    private void updateUBOs() {
        uboService.updateBuffer(uboRepository.cameraViewUBO, uboRepository.cameraViewUBOState, 0);
        uboService.updateBuffer(uboRepository.cameraProjectionUBO, uboRepository.cameraProjectionUBOState, 0);
        uboService.updateBuffer(uboRepository.frameCompositionUBO, uboRepository.frameCompositionUBOState, 0);
        uboService.updateBuffer(uboRepository.lensPostProcessingUBO, uboRepository.lensPostProcessingUBOState, 0);
        uboService.updateBuffer(uboRepository.ssaoUBO, uboRepository.ssaoUBOState, 0);
        uboService.updateBuffer(uboRepository.uberUBO, uboRepository.uberUBOState, 0);
    }
}
