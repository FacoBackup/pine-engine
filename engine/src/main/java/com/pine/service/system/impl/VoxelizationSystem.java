package com.pine.service.system.impl;

import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class VoxelizationSystem extends AbstractSystem {
    private final IntBuffer numTrianglesBuffer = MemoryUtil.memAllocInt(1);
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();
    private UniformDTO numTriangles;
    private boolean shouldRender = true;
    @Override
    public void onInitialize() {
        numTriangles = computeRepository.voxelizationCompute.addUniformDeclaration("numTriangles", GLSLType.INT);
    }

    @Override
    protected boolean isRenderable() {
        return !renderingRepository.requests.isEmpty() && shouldRender;
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(ssboRepository.voxelMetadataSSBO);
        ssboService.bind(ssboRepository.voxelGridSSBO);
        computeService.bind(computeRepository.voxelizationCompute);
        int gridSize = 128;
        COMPUTE_RUNTIME_DATA.groupX = gridSize / 8;
        COMPUTE_RUNTIME_DATA.groupY = gridSize / 8;
        COMPUTE_RUNTIME_DATA.groupZ = gridSize / 8;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_SHADER_STORAGE_BARRIER_BIT;

        RenderingRequest first = renderingRepository.requests.getFirst();
        MeshStreamableResource mesh = first.mesh;

        numTrianglesBuffer.put(0, mesh.triangleCount);
        computeService.bindUniform(numTriangles, numTrianglesBuffer);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, mesh.vertexVBO.getBuffer());
        numTrianglesBuffer.put(0, mesh.vertexCount);
        GL46.glGetBufferParameteriv(GL46.GL_ARRAY_BUFFER, GL46.GL_BUFFER_SIZE, numTrianglesBuffer);

        int vertexSSBO = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, vertexSSBO);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, mesh.vertexCount, GL46.GL_STATIC_DRAW); // Allocate SSBO
        GL46.glCopyBufferSubData(GL46.GL_ARRAY_BUFFER, GL46.GL_SHADER_STORAGE_BUFFER, 0, 0, mesh.vertexCount); // Copy data
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, 0, vertexSSBO);  // Bind to binding point 0

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, mesh.indexVBO);
        numTrianglesBuffer.put(0, mesh.triangleCount);
        GL46.glGetBufferParameteriv(GL46.GL_ELEMENT_ARRAY_BUFFER, GL46.GL_BUFFER_SIZE, numTrianglesBuffer); // Get the size of the index buffer

        int indexSSBO = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, indexSSBO);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, mesh.triangleCount, GL46.GL_STATIC_DRAW); // Allocate SSBO
        GL46.glCopyBufferSubData(GL46.GL_ELEMENT_ARRAY_BUFFER, GL46.GL_SHADER_STORAGE_BUFFER, 0, 0, mesh.triangleCount); // Copy data
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, 1, indexSSBO);  // Bind to binding point 1

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, 0);


        computeService.dispatch(COMPUTE_RUNTIME_DATA);
        shouldRender = false;
    }
}
