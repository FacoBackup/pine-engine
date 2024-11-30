package com.pine.engine.service.system.impl.gbuffer;

import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.repository.terrain.MaterialLayer;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.streaming.ref.MaterialResourceRef;
import com.pine.engine.service.streaming.ref.TextureResourceRef;

import java.util.ArrayList;
import java.util.List;

public class TerrainGBufferPass extends AbstractGBufferPass {
    private UniformDTO textureSize;
    private UniformDTO tilesScaleTranslation;
    private UniformDTO heightScale;
    private UniformDTO terrainOffset;
    private final List<UniformDTO> materials = new ArrayList<>();

    @Override
    public void onInitialize() {
        super.onInitialize();

        materials.add(addUniformDeclaration("material0"));
        materials.add(addUniformDeclaration("material1"));
        materials.add(addUniformDeclaration("material2"));
        materials.add(addUniformDeclaration("material3"));
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
        materialService.bindMaterialLayers(terrainRepository.materialMask, terrainRepository.materialLayers, materials);
        meshService.renderTerrain(textureSize, terrainOffset, heightScale, tilesScaleTranslation);
    }

    @Override
    public String getTitle() {
        return "Terrain";
    }
}
