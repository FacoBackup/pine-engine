package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;

import static com.pine.repository.core.CoreSSBORepository.MAX_INSTANCING;

public class FoliageGBufferPass extends AbstractGBufferPass implements Loggable {
    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferInstanceShader;
    }

    @Override
    protected void renderInternal() {
        prepareCall();
        ssboRepository.foliageTransformationSSBO.setBindingPoint(3);
        ssboService.bind(ssboRepository.foliageTransformationSSBO);
        for(var foliage : terrainRepository.foliage.values()) {
            if(foliage.count < MAX_INSTANCING && foliage.count > 0) {
                var mesh = (MeshResourceRef) streamingService.streamIn(foliage.id, StreamableResourceType.MESH);
                if(mesh != null) {
                    meshService.bind(mesh);
                    meshService.setInstanceCount(foliage.count);
                    meshService.draw();
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Instance GBuffer";
    }

}
