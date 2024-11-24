package com.pine.service.system.impl;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.VoxelChunkResourceRef;
import com.pine.service.system.AbstractPass;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.shader.ShaderService.COMPUTE_RUNTIME_DATA;

public class VoxelVisualizerPass extends AbstractPass {
    private static final int LOCAL_SIZE_X = 8;
    private static final int LOCAL_SIZE_Y = 8;
    private static final int BUFFER_BINDING_POINT = 12;
    private final Vector4f centerScaleBuffer = new Vector4f();
    private final Vector3i settingsBuffer = new Vector3i();
    private UniformDTO centerScale;
    private UniformDTO settings;


    @Override
    public void onInitialize() {
        centerScale = addUniformDeclaration("centerScale");
        settings = addUniformDeclaration("settings");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.voxelRaymarchingCompute;
    }

    @Override
    protected boolean isRenderable() {
        return voxelRepository.showVoxels && renderingRepository.voxelChunksFilled > 0;
    }

    @Override
    protected void renderInternal() {
        bindGlobal();

        var currentSvo = worldService.getCurrentTile().getSvo();
        if (currentSvo != null) {
            var chunk = (VoxelChunkResourceRef) streamingService.streamIn(currentSvo.getId(), StreamableResourceType.VOXEL_CHUNK);
            if (chunk != null) {
                chunk.lastUse = clockRepository.totalTime;

                chunk.getBuffer().setBindingPoint(BUFFER_BINDING_POINT);
                ssboService.bind(chunk.getBuffer());

                centerScaleBuffer.set(
                        chunk.center.x,
                        chunk.center.y,
                        chunk.center.z,
                        chunk.size
                );
                shaderService.bindVec4(centerScaleBuffer, centerScale);
                shaderService.dispatch(COMPUTE_RUNTIME_DATA);
            }
        }

        shaderService.unbind();
    }

    private void bindGlobal() {
        FBO fbo = bufferRepository.postProcessingBuffer;
        fbo.bindForWriting();

        COMPUTE_RUNTIME_DATA.groupX = (fbo.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (fbo.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        settingsBuffer.set(voxelRepository.randomColors ? 1 : 0, voxelRepository.showRaySearchCount ? 1 : 0, voxelRepository.showRayTestCount ? 1 : 0);
        shaderService.bindVec3i(settingsBuffer, settings);
    }

    @Override
    public String getTitle() {
        return "Voxel visualization";
    }
}
