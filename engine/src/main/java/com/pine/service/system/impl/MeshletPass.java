package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.service.meshlet.MeshletInfo;
import com.pine.service.meshlet.MeshletUtil;
import com.pine.service.meshlet.Meshlets;
import com.pine.service.meshlet.TerrainGenerationUtil;
import com.pine.service.resource.shader.ComputeRuntimeData;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

public class MeshletPass extends AbstractPass implements Loggable {
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();
    private static final int LOCAL_SIZE_X = 8;
    private static final int LOCAL_SIZE_Y = 8;

    ShaderStorageBufferObject infoBuffer;
    ShaderStorageBufferObject indexBuffer;
    ShaderStorageBufferObject triangleBuffer;
    ShaderStorageBufferObject vertexBuffer;
    private UniformDTO meshletCount;
    private int meshletCountData;
    private int triangleCount;

    @Override
    public void onInitialize() {
        int size = 50;
        var data = TerrainGenerationUtil.computeMesh(size);
        Meshlets meshlets = MeshletUtil.genMeshlets(data);

        var infoBufferData = MemoryUtil.memAllocInt(4 * meshlets.meshletInfos.size());
        int i = 0;
        for (MeshletInfo meshletInfo : meshlets.meshletInfos) {
            infoBufferData.put(i, meshletInfo.vertexIndexLength);
            i++;
            infoBufferData.put(i, meshletInfo.vertexIndexStart);
            i++;

            infoBufferData.put(i, meshletInfo.trianglesLength);
            i++;

            infoBufferData.put(i, meshletInfo.trianglesStart);
            i++;
        }


        infoBuffer = new ShaderStorageBufferObject(new SSBOCreationData(infoBufferData));
        indexBuffer = new ShaderStorageBufferObject(new SSBOCreationData(meshlets.meshletVertices));
        triangleBuffer = new ShaderStorageBufferObject(new SSBOCreationData(meshlets.meshletTriangles.asIntBuffer()));
        vertexBuffer = new ShaderStorageBufferObject(new SSBOCreationData(meshlets.vertices));

        MemoryUtil.memFree(meshlets.meshletTriangles);
        MemoryUtil.memFree(meshlets.meshletVertices);
        MemoryUtil.memFree(meshlets.vertices);

        meshletCountData = meshlets.meshletInfos.size();
        triangleCount = data.vertices.length / 3;
        meshletCount = addUniformDeclaration("meshletCount");
        getLogger().warn("Meshlet count: {}", meshletCountData);
        getLogger().error("Total triangle count: {}", triangleCount);
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.meshletCompute;
    }

    @Override
    protected void renderInternal() {

        fboRepository.postProcessingBuffer.bindForCompute();

        COMPUTE_RUNTIME_DATA.groupX = triangleCount;//(fboRepository.postProcessingBuffer.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = 1;//(fboRepository.postProcessingBuffer.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        infoBuffer.setBindingPoint(0);
        indexBuffer.setBindingPoint(1);
        triangleBuffer.setBindingPoint(2);
        vertexBuffer.setBindingPoint(3);

        ssboService.bind(infoBuffer);
        ssboService.bind(indexBuffer);
        ssboService.bind(triangleBuffer);
        ssboService.bind(vertexBuffer);

        shaderService.bindInt(meshletCountData, meshletCount);
        shaderService.dispatch(COMPUTE_RUNTIME_DATA);

        shaderService.unbind();
    }

    @Override
    public String getTitle() {
        return "Voxel visualization";
    }
}
