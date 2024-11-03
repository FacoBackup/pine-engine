package com.pine.service.system.impl;

import com.pine.repository.core.CoreFBORepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.system.AbstractPass;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class FoliageCullingPass extends AbstractPass {
    private final static int TIMEOUT = 16;
    private static final int LOCAL_SIZE_X = 1;
    private static final int LOCAL_SIZE_Y = 1;
    private TextureResourceRef heightMap;
    private TextureResourceRef instanceMaskMap;
    private UniformDTO imageSizeU;
    private UniformDTO planeSize;
    private UniformDTO heightScale;
    private long sinceLastRun;
    private final Vector2f imageSize = new Vector2f();

    @Override
    public void onInitialize() {
        imageSizeU = addUniformDeclaration("imageSize");
        planeSize = addUniformDeclaration("planeSize");
        heightScale = addUniformDeclaration("heightScale");
    }

    @Override
    protected boolean isRenderable() {
        if ((clockRepository.totalTime - sinceLastRun) >= TIMEOUT) {
            heightMap = terrainRepository.heightMapTexture != null ? (TextureResourceRef) streamingService.stream(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE) : null;
            instanceMaskMap = heightMap != null && terrainRepository.instanceMaskMap != null ? (TextureResourceRef) streamingService.stream(terrainRepository.instanceMaskMap, StreamableResourceType.TEXTURE) : null;
            return heightMap != null && instanceMaskMap != null;
        }
        return false;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.foliageCullingShader;
    }

    @Override
    protected void renderInternal() {
        sinceLastRun = clockRepository.totalTime;
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
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_BUFFER_UPDATE_BARRIER_BIT | GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        imageSize.x = heightMap.width;
        imageSize.y = heightMap.height;

        instanceMaskMap.bindForReading(0);
        shaderService.bindSampler2dDirect(heightMap, 1);
        shaderService.bindInt(heightMap.width, planeSize);
        shaderService.bindVec2(imageSize, imageSizeU);
        shaderService.bindFloat(terrainRepository.heightScale, heightScale);

        shaderService.dispatch(COMPUTE_RUNTIME_DATA);

        shaderService.unbind();
    }

    @Override
    public String getTitle() {
        return "Instance culling";
    }
}
