package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.service.system.AbstractPass;

public class ShaderDataSyncPass extends AbstractPass implements Loggable {
    @Override
    protected void renderInternal() {
        uboService.updateBuffer(bufferRepository.globalDataUBO, bufferRepository.globalDataBuffer, 0);
        if (!renderingRepository.infoUpdated) {
            return;
        }
        ssboService.updateBuffer(bufferRepository.lightMetadataSSBO, bufferRepository.lightSSBOState, 0);
        renderingRepository.infoUpdated = false;
    }

    @Override
    public String getTitle() {
        return "Data synchronization";
    }
}
