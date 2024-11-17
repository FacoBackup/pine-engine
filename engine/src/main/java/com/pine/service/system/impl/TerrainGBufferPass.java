package com.pine.service.system.impl;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;

public class TerrainGBufferPass extends AbstractGBufferPass {
    private UniformDTO textureSize;
    private UniformDTO tilesScaleTranslation;
    private UniformDTO heightScale;
    private UniformDTO fallbackMaterial;
    private UniformDTO terrainOffset;

    @Override
    public void onInitialize() {
        super.onInitialize();

        textureSize = addUniformDeclaration("textureSize");
        terrainOffset = addUniformDeclaration("terrainOffset");
        tilesScaleTranslation = addUniformDeclaration("tilesScaleTranslation");
        heightScale = addUniformDeclaration("heightScale");
        fallbackMaterial = addUniformDeclaration("fallbackMaterial");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferTerrainShader;
    }

    @Override
    protected boolean isRenderable() {
        return terrainRepository.enabled;
    }

    @Override
    protected void renderInternal() {
        prepareCall();

        var foliageMask = (TextureResourceRef) streamingService.streamIn(terrainRepository.foliageMask, StreamableResourceType.TEXTURE);
        if (foliageMask != null) {
            foliageMask.lastUse = clockRepository.totalTime;
        }

        var heightMap = (TextureResourceRef) streamingService.streamIn(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE);
        if (heightMap != null) {
            heightMap.lastUse = clockRepository.totalTime;
            meshService.renderTerrain(heightMap, textureSize, terrainOffset, heightScale, tilesScaleTranslation, fallbackMaterial);
        }
    }

    @Override
    public String getTitle() {
        return "Terrain";
    }
}
