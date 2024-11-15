package com.pine.service.system.impl;

import com.pine.repository.core.CoreBufferRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.system.AbstractPass;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class FoliageCullingPass extends AbstractPass {
    private final static int TIMEOUT = 16;
    private static final int LOCAL_SIZE_X = 1;
    private static final int LOCAL_SIZE_Y = 1;
    private TextureResourceRef heightMap;
    private TextureResourceRef foliageMask;
    private UniformDTO imageSizeU;
    private UniformDTO terrainOffsetU;
    private UniformDTO heightScale;
    private UniformDTO colorToMatchU;
    private long sinceLastRun;
    private final Vector2f imageSize = new Vector2f();
    private final Vector2f terrainSize = new Vector2f();
    private final IntBuffer atomicCountValue = MemoryUtil.memAllocInt(1);

    @Override
    public void onInitialize() {
        imageSizeU = addUniformDeclaration("imageSize");
        heightScale = addUniformDeclaration("heightScale");
        colorToMatchU = addUniformDeclaration("colorToMatch");
        terrainOffsetU = addUniformDeclaration("terrainOffset");
    }

    @Override
    protected boolean isRenderable() {
        if (terrainRepository.enabled && terrainRepository.foliage.isEmpty()) {
            return false;
        }
        if ((clockRepository.totalTime - sinceLastRun) >= TIMEOUT) {
            heightMap = terrainRepository.heightMapTexture != null ? (TextureResourceRef) streamingService.streamIn(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE) : null;
            foliageMask = heightMap != null && terrainRepository.foliageMask != null ? (TextureResourceRef) streamingService.streamIn(terrainRepository.foliageMask, StreamableResourceType.TEXTURE) : null;
            return heightMap != null && foliageMask != null;
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

        ssboRepository.foliageTransformationSSBO.setBindingPoint(3);
        ssboService.bind(ssboRepository.foliageTransformationSSBO);

        GL46.glBindBufferBase(GL46.GL_ATOMIC_COUNTER_BUFFER, 2, bufferRepository.atomicCounterBuffer);
        GL46.glBufferSubData(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, CoreBufferRepository.ZERO);

        COMPUTE_RUNTIME_DATA.groupX = (foliageMask.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (foliageMask.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_BUFFER_UPDATE_BARRIER_BIT | GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        shaderService.bindSampler2dDirect(foliageMask, 0);
        shaderService.bindSampler2dDirect(heightMap, 1);
        shaderService.bindFloat(terrainRepository.heightScale, heightScale);

        terrainSize.x = terrainRepository.offsetX;
        terrainSize.y = terrainRepository.offsetZ;
        shaderService.bindVec2(terrainSize, terrainOffsetU);

        imageSize.x = foliageMask.width;
        imageSize.y = foliageMask.height;
        shaderService.bindVec2(imageSize, imageSizeU);

        int offset = 0;
        for (var foliage : terrainRepository.foliage.values()) {
            // TODO - ONE RUN PER FRAME INSTEAD OF EVERYTHING ALL AT ONCE
            shaderService.bindVec3(foliage.color, colorToMatchU);

            shaderService.dispatch(COMPUTE_RUNTIME_DATA);

            GL46.glGetBufferSubData(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, atomicCountValue);

            foliage.count = atomicCountValue.get(0) - offset;
            foliage.offset = offset;
            offset = atomicCountValue.get(0);
        }
    }

    @Override
    public String getTitle() {
        return "Instance culling";
    }
}
