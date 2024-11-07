package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.service.system.AbstractPass;
import org.joml.Vector3f;

public class ShaderDataSyncPass extends AbstractPass implements Loggable {
    @Override
    protected void renderInternal() {
        updateGlobalBuffer();
        // TODO - Find a more optimized way to update the buffer
        updateSSBOs();
        renderingRepository.sync();
    }

    private void updateSSBOs() {
        ssboService.updateBuffer(ssboRepository.transformationSSBO, ssboRepository.transformationSSBOState, 0);
        ssboService.updateBuffer(ssboRepository.lightMetadataSSBO, ssboRepository.lightSSBOState, 0);
    }

    private void updateGlobalBuffer() {

        Vector3f sunLightDirection = new Vector3f((float) Math.sin(atmosphere.elapsedTime), (float) Math.cos(atmosphere.elapsedTime), 0).mul(100);
        Vector3f sunLightColor = atmosphere.sunLightColor;

        bufferRepository.globalDataBuffer.put(87, atmosphere.elapsedTime);

        bufferRepository.globalDataBuffer.put(88, sunLightDirection.x);
        bufferRepository.globalDataBuffer.put(89, sunLightDirection.y);
        bufferRepository.globalDataBuffer.put(90, sunLightDirection.z);
        bufferRepository.globalDataBuffer.put(91, 0);

        bufferRepository.globalDataBuffer.put(92, sunLightColor.x);
        bufferRepository.globalDataBuffer.put(93, sunLightColor.y);
        bufferRepository.globalDataBuffer.put(94, sunLightColor.z);

        uboService.updateBuffer(bufferRepository.globalDataUBO, bufferRepository.globalDataBuffer, 0);
    }

    @Override
    public String getTitle() {
        return "Data synchronization";
    }
}
