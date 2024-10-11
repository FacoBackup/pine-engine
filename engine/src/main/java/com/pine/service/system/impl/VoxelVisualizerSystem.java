package com.pine.service.system.impl;

import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class VoxelVisualizerSystem extends AbstractSystem {
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();
    private final FloatBuffer centerScaleBuffer = MemoryUtil.memAllocFloat(4);
    private UniformDTO centerScale;

    @Override
    public void onInitialize() {
        centerScale = computeRepository.voxelRaymarchingCompute.addUniformDeclaration("centerScale", GLSLType.VEC_4);
    }

    @Override
    protected boolean isRenderable() {
        return voxelizerRepository.octreeSSBO != null;
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(voxelizerRepository.octreeSSBO);
         computeService.bind(computeRepository.voxelRaymarchingCompute);

        fboRepository.auxBuffer.bindForCompute();

        centerScaleBuffer.put(0, voxelizerRepository.center.x);
        centerScaleBuffer.put(1, voxelizerRepository.center.y);
        centerScaleBuffer.put(2, voxelizerRepository.center.z);
        centerScaleBuffer.put(3, voxelizerRepository.gridResolution);
        computeService.bindUniform(centerScale, centerScaleBuffer);

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
