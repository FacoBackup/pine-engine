package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.service.system.AbstractPass;
import org.joml.Vector3f;

public class ShaderDataSyncPass extends AbstractPass implements Loggable {
    @Override
    protected void renderInternal() {
        uboService.updateBuffer(bufferRepository.globalDataUBO, bufferRepository.globalDataBuffer, 0);
        if (!renderingRepository.infoUpdated) {
            return;
        }
        ssboService.updateBuffer(ssboRepository.lightMetadataSSBO, ssboRepository.lightSSBOState, 0);
        renderingRepository.infoUpdated = false;
        renderingRepository.sync();
    }

    @Override
    public String getTitle() {
        return "Data synchronization";
    }
}
