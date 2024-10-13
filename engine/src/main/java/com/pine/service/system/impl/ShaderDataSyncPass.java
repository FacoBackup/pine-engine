package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.service.system.AbstractPass;

public class ShaderDataSyncPass extends AbstractPass implements Loggable {
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
    }

    @Override
    public String getTitle() {
        return "Data synchronization";
    }
}
