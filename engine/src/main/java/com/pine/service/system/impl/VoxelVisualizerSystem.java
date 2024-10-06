package com.pine.service.system.impl;

import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.opengl.GL46;

public class VoxelVisualizerSystem extends AbstractSystem {
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();

    @Override
    protected boolean isRenderable() {
        return voxelizerRepository.octreeSSBO != null;
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(voxelizerRepository.octreeSSBO);
        ssboService.bind(voxelizerRepository.voxelDataSSBO);
        computeService.bind(computeRepository.voxelRaymarchingCompute);

        fboRepository.auxBuffer.bindForCompute();

        COMPUTE_RUNTIME_DATA.groupX = fboRepository.auxBuffer.width;
        COMPUTE_RUNTIME_DATA.groupY = fboRepository.auxBuffer.height;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
        computeService.dispatch(COMPUTE_RUNTIME_DATA);
    }
}
