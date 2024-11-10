package com.pine.service.system.impl;

import com.pine.repository.FoliageInstance;
import com.pine.repository.core.CoreBufferRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.Tile;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.system.AbstractPass;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static com.pine.service.grid.HashGrid.TILE_SIZE;
import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class FoliageCullingPass extends AbstractPass {
    private final static int TIMEOUT = 16;
    private static final int LOCAL_SIZE_X = 1;
    private static final int LOCAL_SIZE_Y = 1;
    private UniformDTO imageSizeU;
    private UniformDTO tileOffset;
    private UniformDTO heightScale;
    private UniformDTO colorToMatchU;
    private long sinceLastRun;
    private final Vector2f imageSize = new Vector2f();
    private final IntBuffer atomicCountValue = MemoryUtil.memAllocInt(1);
    private int offset = 0;

    @Override
    public void onInitialize() {
        tileOffset = addUniformDeclaration("tileOffset");
        imageSizeU = addUniformDeclaration("imageSize");
        heightScale = addUniformDeclaration("heightScale");
        colorToMatchU = addUniformDeclaration("colorToMatch");
    }

    @Override
    protected boolean isRenderable() {
        return (clockRepository.totalTime - sinceLastRun) >= TIMEOUT;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.foliageCullingShader;
    }

    @Override
    protected void renderInternal() {
        sinceLastRun = clockRepository.totalTime;
        offset = 0;

        GL46.glBindBufferBase(GL46.GL_ATOMIC_COUNTER_BUFFER, 2, bufferRepository.atomicCounterBuffer);
        GL46.glBufferSubData(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, CoreBufferRepository.ZERO);

        for (var foliage : terrainRepository.foliage.values()) {
            // TODO - ONE RUN PER FRAME INSTEAD OF EVERYTHING ALL AT ONCE
            for (var tile : hashGridService.getLoadedTiles()) {
                if (tile != null && tile.isTerrainPresent) {
                    var heightMap = (TextureResourceRef) streamingService.streamIn(tile.terrainHeightMapId, StreamableResourceType.TEXTURE);
                    var foliageMask = (TextureResourceRef) streamingService.streamIn(tile.terrainFoliageId, StreamableResourceType.TEXTURE);
                    if (heightMap != null && foliageMask != null) {
                        foliageMask.lastUse = heightMap.lastUse = sinceLastRun;
                        runForTexture(foliage, tile, heightMap, foliageMask);
                    }
                }
            }

            GL46.glGetBufferSubData(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, atomicCountValue);
            foliage.count = atomicCountValue.get(0) - offset;
            foliage.offset = offset;
            offset = atomicCountValue.get(0);
        }
    }

    private void runForTexture(FoliageInstance foliage, Tile tile, TextureResourceRef heightMap, TextureResourceRef foliageMask) {
        ssboRepository.foliageTransformationSSBO.setBindingPoint(3);
        ssboService.bind(ssboRepository.foliageTransformationSSBO);

        COMPUTE_RUNTIME_DATA.groupX = (foliageMask.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (foliageMask.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_BUFFER_UPDATE_BARRIER_BIT | GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        shaderService.bindSampler2dDirect(foliageMask, 0);
        shaderService.bindSampler2dDirect(heightMap, 1);
        shaderService.bindFloat(terrainRepository.heightScale, heightScale);

        imageSize.x = heightMap.width;
        imageSize.y = heightMap.height;
        shaderService.bindVec2(imageSize, imageSizeU);

        imageSize.x = tile.getX() * TILE_SIZE - TILE_SIZE / 2f;
        imageSize.y = tile.getZ() * TILE_SIZE - TILE_SIZE / 2f;
        shaderService.bindVec2(imageSize, tileOffset);

        shaderService.bindVec3(foliage.color, colorToMatchU);

        shaderService.dispatch(COMPUTE_RUNTIME_DATA);

    }

    @Override
    public String getTitle() {
        return "Instance culling";
    }
}
