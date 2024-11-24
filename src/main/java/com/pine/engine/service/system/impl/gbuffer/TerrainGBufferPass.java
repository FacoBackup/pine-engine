package com.pine.engine.service.system.impl.gbuffer;

import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
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

        bindTexture(terrainRepository.materialMask, 4);
        int index = 5;
        int matIndex = 0;
        for (var material : terrainRepository.materials.values()) {

            shaderService.bindVec3(material.color, materials.get(matIndex));
            matIndex++;

            bindTexture(material.albedo, index);
            index++;
            bindTexture(material.roughness, index);
            index++;
            bindTexture(material.metallic, index);
            index++;
            bindTexture(material.normal, index);
            index++;
        }
        meshService.renderTerrain(textureSize, terrainOffset, heightScale, tilesScaleTranslation);
    }

    private void bindTexture(String sampler, int index) {
        var normal = (TextureResourceRef) streamingService.streamIn(sampler, StreamableResourceType.TEXTURE);
        if (normal != null) {
            shaderService.bindSampler2dDirect(normal.texture, index);
            normal.lastUse = clockRepository.totalTime;
        }
    }

    @Override
    public String getTitle() {
        return "Terrain";
    }
}
