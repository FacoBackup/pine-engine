package com.pine.service.system.impl;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class InstanceGBufferPass extends AbstractGBufferPass {
    private UniformDTO probeFilteringLevels;
    private UniformDTO debugShadingMode;
    private final IntBuffer instanceCountBuffer = MemoryUtil.memAllocInt(1);

    @Override
    public void onInitialize() {
        probeFilteringLevels = addUniformDeclaration("probeFilteringLevels");
        debugShadingMode = addUniformDeclaration("debugShadingMode");
    }

    @Override
    protected UniformDTO probeFilteringLevels() {
        return probeFilteringLevels;
    }

    @Override
    protected UniformDTO debugShadingMode() {
        return debugShadingMode;
    }

    @Override
    protected boolean isRenderable() {
        var heightMap = terrainRepository.heightMapTexture != null ? (TextureResourceRef) streamingService.stream(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE) : null;
        return heightMap != null;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferInstanceShader;
    }

    @Override
    protected void renderInternal() {
        instanceCountBuffer.position(0);
        instanceCountBuffer.put(0, 0);
        GL46.glGetNamedBufferSubData(ssboRepository.instancingMetadataSSBO.getBuffer(), 0, instanceCountBuffer);

        meshService.bind(meshRepository.cubeMesh);
        meshService.setInstanceCount(0);
        meshService.draw();
    }

    @Override
    public String getTitle() {
        return "Instance GBuffer";
    }

}
