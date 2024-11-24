package com.pine.service.system.impl;

import com.pine.repository.core.CoreBufferRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.terrain.FoliageInstance;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.system.AbstractPass;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.shader.ShaderService.COMPUTE_RUNTIME_DATA;

public class FoliageCullingPass extends AbstractPass {
    private TextureResourceRef heightMap;
    private TextureResourceRef foliageMask;
    private UniformDTO imageSizeU;
    private UniformDTO settingsU;
    private UniformDTO terrainOffsetU;
    private UniformDTO heightScale;
    private UniformDTO colorToMatchU;
    private final Vector2f imageSize = new Vector2f();
    private final Vector4f settings = new Vector4f();

    @Override
    public void onInitialize() {
        imageSizeU = addUniformDeclaration("imageSize");
        settingsU = addUniformDeclaration("settings");
        heightScale = addUniformDeclaration("heightScale");
        colorToMatchU = addUniformDeclaration("colorToMatch");
        terrainOffsetU = addUniformDeclaration("terrainOffset");
    }

    @Override
    protected boolean isRenderable() {
        if (terrainRepository.enabled && terrainRepository.foliage.isEmpty()) {
            return false;
        }
        heightMap = terrainRepository.heightMapTexture != null ? (TextureResourceRef) streamingService.streamIn(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE) : null;
        foliageMask = heightMap != null && terrainRepository.foliageMask != null ? (TextureResourceRef) streamingService.streamIn(terrainRepository.foliageMask, StreamableResourceType.TEXTURE) : null;
        return heightMap != null && foliageMask != null;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.foliageCullingCompute;
    }

    @Override
    protected void renderInternal() {
        COMPUTE_RUNTIME_DATA.groupX = 256;
        COMPUTE_RUNTIME_DATA.groupY = 256;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_COMMAND_BARRIER_BIT | GL46.GL_SHADER_STORAGE_BARRIER_BIT;

        shaderService.bindSampler2dDirect(foliageMask, 0);
        shaderService.bindSampler2dDirect(heightMap, 1);
        shaderService.bindFloat(terrainRepository.heightScale, heightScale);

        shaderService.bindVec2(terrainRepository.offset, terrainOffsetU);

        imageSize.x = heightMap.width;
        imageSize.y = heightMap.height;
        shaderService.bindVec2(imageSize, imageSizeU);

        for (var foliage : terrainRepository.foliage.values()) {
            boolean isNotReady = isFoliageNotReady(foliage);
            if (foliage.isNotFrozen() || isNotReady) {
                if (foliage.prevMaximumNumberOfInstances != foliage.maximumNumberOfInstances) {
                    initializeTransformationBuffer(foliage);
                }
                if (isNotReady) {
                    initializeBuffers(foliage);
                }
                foliage.freezeVersion();
            }
            if (isFoliageNotReady(foliage)) {
                continue;
            }
            var mesh = streamingService.streamIn(foliage.mesh, StreamableResourceType.MESH);
            if (mesh != null) {
                mesh.lastUse = clockRepository.totalTime;

                GL46.glBindBufferBase(GL46.GL_ATOMIC_COUNTER_BUFFER, 2, foliage.atomicCounterBuffer);
                GL46.glBufferSubData(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, CoreBufferRepository.ZERO);
                GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, 3, foliage.transformationsBuffer);
                GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, 4, foliage.indirectDrawBuffer);

                settings.x = foliage.maxDistanceFromCamera;
                settings.y = foliage.maximumNumberOfInstances;
                settings.z = ((MeshResourceRef) mesh).indicesCount;
                shaderService.bindVec4(settings, settingsU);
                shaderService.bindVec3(foliage.color, colorToMatchU);

                shaderService.dispatch(COMPUTE_RUNTIME_DATA);
            }
        }
    }

    public static boolean isFoliageNotReady(FoliageInstance foliage) {
        return foliage.indirectDrawBuffer == null || foliage.transformationsBuffer == null || foliage.atomicCounterBuffer == null;
    }

    private void initializeTransformationBuffer(FoliageInstance foliage) {
        if (foliage.transformationsBuffer != null) {
            GL46.glDeleteBuffers(foliage.transformationsBuffer);
        }
        foliage.transformationsBuffer = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, foliage.transformationsBuffer);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, (long) foliage.maximumNumberOfInstances * GLSLType.FLOAT.getSize() * 3, GL46.GL_DYNAMIC_DRAW);
    }

    private void initializeBuffers(FoliageInstance foliage) {
        // ATOMIC COUNTER
        foliage.atomicCounterBuffer = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ATOMIC_COUNTER_BUFFER, foliage.atomicCounterBuffer);
        GL46.glBufferData(GL46.GL_ATOMIC_COUNTER_BUFFER, Integer.BYTES, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBufferBase(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, foliage.atomicCounterBuffer);

        // DRAW INDIRECT BUFFER
        foliage.indirectDrawBuffer = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, foliage.indirectDrawBuffer);
        GL46.glBufferData(GL46.GL_DRAW_INDIRECT_BUFFER, 5 * Integer.BYTES, GL46.GL_DYNAMIC_DRAW);

        initializeTransformationBuffer(foliage);
    }

    @Override
    public String getTitle() {
        return "Foliage culling";
    }
}
