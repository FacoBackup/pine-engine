package com.pine.service.system.impl;

import com.pine.Loggable;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class ShaderDataSyncSystem extends AbstractSystem implements Loggable {
    @Override
    protected void renderInternal() {
        updateUBOs();

        if (renderingRepository.infoUpdated) {
            renderingRepository.infoUpdated = false;
            renderingRepository.switchRequests();

            updateSSBOs();
        }
    }

    private void updateSSBOs() {
        ssboService.updateBuffer(ssboRepository.transformationSSBO, ssboRepository.transformationSSBOState, 0);
        ssboService.updateBuffer(ssboRepository.lightMetadataSSBO, ssboRepository.lightSSBOState, 0);
    }

    private void updateUBOs() {
        uboService.updateBuffer(uboRepository.cameraViewUBO, uboRepository.cameraViewUBOState, 0);
        uboService.updateBuffer(uboRepository.cameraProjectionUBO, uboRepository.cameraProjectionUBOState, 0);
        uboService.updateBuffer(uboRepository.lensPostProcessingUBO, uboRepository.lensPostProcessingUBOState, 0);
        uboService.updateBuffer(uboRepository.ssaoUBO, uboRepository.ssaoUBOState, 0);
    }
}
