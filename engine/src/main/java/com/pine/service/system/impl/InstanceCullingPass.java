package com.pine.service.system.impl;

import com.pine.repository.core.CoreFBORepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class InstanceCullingPass extends AbstractPass {
    private static final int LOCAL_SIZE_X = 1;
    private static final int LOCAL_SIZE_Y = 1;
    private TextureResourceRef heightMap;
    private TextureResourceRef instanceMaskMap;
    private UniformDTO planeSize;
    private UniformDTO heightScale;

    @Override
    public void onInitialize() {
        planeSize = addUniformDeclaration("planeSize");
        heightScale = addUniformDeclaration("heightScale");
    }

    @Override
    protected boolean isRenderable() {
        heightMap = terrainRepository.heightMapTexture != null ? (TextureResourceRef) streamingService.stream(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE) : null;
        instanceMaskMap = heightMap != null && terrainRepository.instanceMaskMap != null ? (TextureResourceRef) streamingService.stream(terrainRepository.instanceMaskMap, StreamableResourceType.TEXTURE) : null;
        return heightMap != null && instanceMaskMap != null;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.instanceCullingShader;
    }

    @Override
    protected void renderInternal() {
        ssboRepository.instancingMetadata.put(0, 0);
        ssboService.updateBuffer(ssboRepository.instancingMetadataSSBO, ssboRepository.instancingMetadata, 0);

        ssboRepository.instancingMetadataSSBO.setBindingPoint(3);
        ssboRepository.instancingTransformationSSBO.setBindingPoint(4);

        ssboService.bind(ssboRepository.instancingMetadataSSBO);
        ssboService.bind(ssboRepository.instancingTransformationSSBO);

        GL46.glBindBufferBase(GL46.GL_ATOMIC_COUNTER_BUFFER, 2, fboRepository.atomicCounterBuffer);
        GL46.glBufferSubData(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, CoreFBORepository.ZERO);

        COMPUTE_RUNTIME_DATA.groupX = (instanceMaskMap.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (instanceMaskMap.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_BUFFER_UPDATE_BARRIER_BIT;

        shaderService.bindSampler2dDirect(instanceMaskMap, 0);
        shaderService.bindSampler2dDirect(heightMap, 1);
        shaderService.bindInt(heightMap.width, planeSize);
        shaderService.bindFloat(terrainRepository.heightScale, heightScale);

        shaderService.dispatch(COMPUTE_RUNTIME_DATA);
        shaderService.unbind();
        ssboService.unbind();
    }

    @Override
    public String getTitle() {
        return "Instance culling";
    }
}
