package com.pine.service.system.impl.gbuffer;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;

public class TerrainGBufferPass extends AbstractGBufferPass {
    private UniformDTO textureSize;
    private UniformDTO tilesScaleTranslation;
    private UniformDTO heightScale;
    private UniformDTO terrainOffset;

    @Override
    public void onInitialize() {
        super.onInitialize();

        textureSize = addUniformDeclaration("textureSize");
        terrainOffset = addUniformDeclaration("terrainOffset");
        tilesScaleTranslation = addUniformDeclaration("tilesScaleTranslation");
        heightScale = addUniformDeclaration("heightScale");
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

        MaterialResourceRef material = (MaterialResourceRef) streamingService.streamIn(terrainRepository.material, StreamableResourceType.MATERIAL);
        bindMaterial(material);
        meshService.renderTerrain(textureSize, terrainOffset, heightScale, tilesScaleTranslation);
    }

    @Override
    public String getTitle() {
        return "Terrain";
    }
}
