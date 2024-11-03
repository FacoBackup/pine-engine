package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static com.pine.repository.core.CoreSSBORepository.MAX_INSTANCING;

public class FoliageGBufferPass extends AbstractGBufferPass implements Loggable {
    private UniformDTO probeFilteringLevels;
    private UniformDTO debugShadingMode;
    private TextureResourceRef instanceMaskMap;

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
        instanceMaskMap = heightMap != null && terrainRepository.instanceMaskMap != null ? (TextureResourceRef) streamingService.stream(terrainRepository.instanceMaskMap, StreamableResourceType.TEXTURE) : null;
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

        instanceMaskMap.lastUse = clockRepository.totalTime;

        ssboRepository.foliageTransformationSSBO.setBindingPoint(3);
        ssboService.bind(ssboRepository.foliageTransformationSSBO);

        for(var foliage : terrainRepository.foliage.values()) {
            if(foliage.count < MAX_INSTANCING && foliage.count > 0) {
                var mesh = (MeshResourceRef) streamingService.stream(foliage.id, StreamableResourceType.MESH);
                if(mesh != null) {
                    meshService.bind(mesh);
                    meshService.setInstanceCount(foliage.count);
                    meshService.draw();
                    if (c >= 200) {
                        getLogger().warn("Instanced {}", foliage.count);
                        c = 0;
                    }
                    c++;
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Instance GBuffer";
    }

}
