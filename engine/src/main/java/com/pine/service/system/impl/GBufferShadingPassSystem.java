package com.pine.service.system.impl;

import com.pine.Loggable;
import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class GBufferShadingPassSystem extends AbstractSystem implements Loggable {
    private final IntBuffer lightCountBuffer = MemoryUtil.memAllocInt(1);
    private UniformDTO lightCount;
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();

    @Override
    public void onInitialize() {
        lightCount = computeRepository.gBufferShadingCompute.addUniformDeclaration("lightCount", GLSLType.INT);
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(ssboRepository.lightMetadataSSBO);
        computeService.bind(computeRepository.gBufferShadingCompute);

        lightCountBuffer.put(0, renderingRepository.lightCount);
        computeService.bindUniform(lightCount, lightCountBuffer);

        fboRepository.auxBuffer.bindForCompute(fboRepository.gBufferMetallicRoughnessAO, GL46.GL_RGBA16F, GL46.GL_READ_ONLY, 0);
        fboRepository.auxBuffer.bindForCompute(fboRepository.gBufferAlbedoEmissive, GL46.GL_RGBA16F, GL46.GL_READ_ONLY, 1);
        fboRepository.auxBuffer.bindForCompute(fboRepository.gBufferNormal, GL46.GL_RGBA16F, GL46.GL_READ_ONLY, 2);
        fboRepository.auxBuffer.bindForCompute(fboRepository.gBufferDepth, GL46.GL_R16F, GL46.GL_READ_ONLY, 3);
        fboRepository.auxBuffer.bindForCompute(fboRepository.auxSampler, GL46.GL_RGBA16F, GL46.GL_WRITE_ONLY, 4);

        COMPUTE_RUNTIME_DATA.groupX = (fboRepository.auxBuffer.width + 15) / 16;
        COMPUTE_RUNTIME_DATA.groupY = (fboRepository.auxBuffer.height + 15) / 16;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_NONE;
        computeService.dispatch(COMPUTE_RUNTIME_DATA);
    }
}
