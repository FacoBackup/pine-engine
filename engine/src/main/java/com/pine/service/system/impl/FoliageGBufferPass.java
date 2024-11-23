package com.pine.service.system.impl;

import com.pine.repository.core.CoreBufferRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;


public class FoliageGBufferPass extends AbstractGBufferPass {
    private UniformDTO terrainOffsetU;
    private UniformDTO transformOffset;
    private UniformDTO objectScale;

    @Override
    public void onInitialize() {
        super.onInitialize();
        terrainOffsetU = addUniformDeclaration("terrainOffset");
        transformOffset = addUniformDeclaration("transformOffset");
        objectScale = addUniformDeclaration("objectScale");
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
        shaderService.bindSampler2dDirect(bufferRepository.noiseSampler, 10);
        for (var foliage : terrainRepository.foliage.values()) {
            if (foliage.count < CoreBufferRepository.MAX_INSTANCING && foliage.count > 0) {
                var mesh = (MeshResourceRef) streamingService.streamIn(foliage.mesh, StreamableResourceType.MESH);
                var material = (MaterialResourceRef) streamingService.streamIn(foliage.material, StreamableResourceType.MATERIAL);
                bindMaterial(material);
                if (mesh != null) {
                    shaderService.bindVec3(foliage.objectScale, objectScale);
                    shaderService.bindInt(foliage.offset, transformOffset);
                    meshService.bind(mesh);
                    meshService.setInstanceCount(foliage.count);
                    meshService.draw();
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Foliage rendering";
    }
}
