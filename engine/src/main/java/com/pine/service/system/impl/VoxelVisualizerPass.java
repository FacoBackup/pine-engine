package com.pine.service.system.impl;

import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VoxelVisualizerPass extends AbstractPass {
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();
    private static final int LOCAL_SIZE_X = 8;
    private static final int LOCAL_SIZE_Y = 8;
    private static final int BUFFER_BINDING_POINT = 12;
    private final FloatBuffer centerScaleBuffer = MemoryUtil.memAllocFloat(4);
    private final IntBuffer settingsBuffer = MemoryUtil.memAllocInt(3);
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
        for (var chunk : renderingRepository.voxelChunks) {
            if (chunk != null && chunk.getQuantity() > 1) {
                bindGlobal();

                chunk.lastUse = clockRepository.totalTime;

                chunk.getBuffer().setBindingPoint(BUFFER_BINDING_POINT);
                ssboService.bind(chunk.getBuffer());

                centerScaleBuffer.put(0, chunk.center.x);
                centerScaleBuffer.put(1, chunk.center.y);
                centerScaleBuffer.put(2, chunk.center.z);
                centerScaleBuffer.put(3, chunk.size);
                computeService.bindUniform(centerScale, centerScaleBuffer);

                computeService.dispatch(COMPUTE_RUNTIME_DATA);
            }
        }
    }

    private void bindGlobal() {
        computeService.bind(computeRepository.voxelRaymarchingCompute);
        fboRepository.auxBuffer.bindForCompute();

        COMPUTE_RUNTIME_DATA.groupX = (fboRepository.auxBuffer.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (fboRepository.auxBuffer.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_NONE;

        settingsBuffer.put(0, voxelRepository.randomColors ? 1 : 0);
        settingsBuffer.put(1, voxelRepository.showRaySearchCount ? 1 : 0);
        settingsBuffer.put(2, voxelRepository.showRayTestCount ? 1 : 0);
        computeService.bindUniform(settings, settingsBuffer);
    }

    @Override
    public String getTitle() {
        return "Voxel visualization";
    }
}
