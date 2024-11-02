package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class InstanceGBufferPass extends AbstractGBufferPass implements Loggable {
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
        var instanceMaskMap = heightMap != null && terrainRepository.instanceMaskMap != null ? (TextureResourceRef) streamingService.stream(terrainRepository.instanceMaskMap, StreamableResourceType.TEXTURE) : null;
        return heightMap != null && instanceMaskMap != null;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferInstanceShader;
    }

    int c = 0;

    @Override
    protected void renderInternal() {
        prepareCall();

        ssboRepository.instancingTransformationSSBO.setBindingPoint(3);
        ssboService.bind(ssboRepository.instancingTransformationSSBO);

        instanceCountBuffer.put(0, 0);
        instanceCountBuffer.position(0);
        GL46.glGetNamedBufferSubData(ssboRepository.instancingMetadataSSBO.getBuffer(), 0, instanceCountBuffer);

        if(instanceCountBuffer.get(0) > 0) {
            meshService.bind(meshRepository.cubeMesh);
            meshService.setInstanceCount(Math.min(instanceCountBuffer.get(0), 100));
            meshService.draw();
            if(c >= 700) {
                getLogger().warn("Instanced {}", instanceCountBuffer.get(0));
                c = 0;
            }
            c++;
        }
    }

    @Override
    public String getTitle() {
        return "Instance GBuffer";
    }

}
