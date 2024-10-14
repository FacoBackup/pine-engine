package com.pine.service.system.impl;

import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VoxelVisualizerPass extends AbstractPass {
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();
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
        return ssboRepository.octreeSSBO != null;
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(ssboRepository.octreeSSBO);
        computeService.bind(computeRepository.voxelRaymarchingCompute);

        fboRepository.auxBuffer.bindForCompute();

        centerScaleBuffer.put(0, voxelRepository.center.x);
        centerScaleBuffer.put(1, voxelRepository.center.y);
        centerScaleBuffer.put(2, voxelRepository.center.z);
        centerScaleBuffer.put(3, voxelRepository.gridResolution);
        computeService.bindUniform(centerScale, centerScaleBuffer);

        settingsBuffer.put(0, voxelRepository.randomColors ? 1 : 0);
        settingsBuffer.put(1, voxelRepository.showRaySearchCount ? 1 : 0);
        settingsBuffer.put(2, voxelRepository.showRayTestCount ? 1 : 0);
        computeService.bindUniform(settings, settingsBuffer);

        COMPUTE_RUNTIME_DATA.groupX = fboRepository.auxBuffer.width;
        COMPUTE_RUNTIME_DATA.groupY = fboRepository.auxBuffer.height;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
        computeService.dispatch(COMPUTE_RUNTIME_DATA);
    }

    @Override
    public String getTitle() {
        return "Voxel visualization";
    }
}
