package com.pine.service.system.impl;

import com.pine.repository.core.CoreBufferRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MeshResourceRef;


public class FoliageGBufferPass extends AbstractGBufferPass {
    private UniformDTO fallbackMaterial;
    private UniformDTO terrainOffsetU;

    @Override
    public void onInitialize() {
        super.onInitialize();
        fallbackMaterial = addUniformDeclaration("fallbackMaterial");
        terrainOffsetU = addUniformDeclaration("terrainOffset");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferFoliageShader;
    }

    @Override
    protected boolean isRenderable() {
        return terrainRepository.enabled;
    }

    @Override
    protected void renderInternal() {
        prepareCall();
        bufferRepository.foliageTransformationSSBO.setBindingPoint(3);
        ssboService.bind(bufferRepository.foliageTransformationSSBO);
        shaderService.bindVec2(terrainRepository.offset, terrainOffsetU);
        shaderService.bindBoolean(true, fallbackMaterial);
        shaderService.bindSampler2dDirect(bufferRepository.noiseSampler, 10);
        for(var foliage : terrainRepository.foliage.values()) {
            if(foliage.count < CoreBufferRepository.MAX_INSTANCING && foliage.count > 0) {
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
