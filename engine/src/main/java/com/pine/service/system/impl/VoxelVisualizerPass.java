package com.pine.service.system.impl;

import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VoxelVisualizerPass extends AbstractPass {
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();
    private static final int LOCAL_SIZE_X = 8;
    private static final int LOCAL_SIZE_Y = 8;
    private static final int BUFFER_BINDING_POINT = 12;
    private final Vector4f centerScaleBuffer = new Vector4f();
    private final Vector3i settingsBuffer = new Vector3i();
    private UniformDTO centerScale;
    private UniformDTO settings;


    @Override
    public void onInitialize() {
        centerScale = computeRepository.voxelRaymarchingCompute.addUniformDeclaration("centerScale", GLSLType.VEC_4);
        settings = computeRepository.voxelRaymarchingCompute.addUniformDeclaration("settings", GLSLType.IVEC_3);
    }

    @Override
    protected boolean isRenderable() {
        return renderingRepository.voxelChunksFilled > 0;
    }

    @Override
    protected void renderInternal() {
        bindGlobal();

        for (var chunk : renderingRepository.voxelChunks) {
            if (chunk != null && chunk.getQuantity() > 1) {
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
                computeService.dispatch(COMPUTE_RUNTIME_DATA);
            }
        }
        computeService.unbind();
    }

    private void bindGlobal() {
        computeService.bind(computeRepository.voxelRaymarchingCompute);
        fboRepository.auxBuffer.bindForCompute();

        COMPUTE_RUNTIME_DATA.groupX = (fboRepository.auxBuffer.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (fboRepository.auxBuffer.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_NONE;

        settingsBuffer.set(voxelRepository.randomColors ? 1 : 0, voxelRepository.showRaySearchCount ? 1 : 0, voxelRepository.showRayTestCount ? 1 : 0);
        shaderService.bindVec3i(settingsBuffer, settings);
    }

    @Override
    public String getTitle() {
        return "Voxel visualization";
    }
}
