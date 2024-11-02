package com.pine.service.system.impl;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;

public class TerrainGBufferPass extends AbstractGBufferPass {
    private UniformDTO debugShadingMode;
    private MeshResourceRef mesh;
    private UniformDTO probeFilteringLevels;
    private TextureResourceRef heightMap;
    private UniformDTO planeSize;
    private UniformDTO heightMapU;
    private UniformDTO heightScale;
    private UniformDTO fallbackMaterial;

    @Override
    public void onInitialize() {
        debugShadingMode = addUniformDeclaration("debugShadingMode");
        probeFilteringLevels = addUniformDeclaration("probeFilteringLevels");
        planeSize = addUniformDeclaration("planeSize");
        heightMapU = addUniformDeclaration("heightMap");
        heightScale = addUniformDeclaration("heightScale");
        fallbackMaterial = addUniformDeclaration("fallbackMaterial");
    }

    @Override
    protected boolean isRenderable() {
        mesh = terrainRepository.bakeId != null ? (MeshResourceRef) streamingService.stream(terrainRepository.bakeId, StreamableResourceType.MESH) : null;
        heightMap = mesh != null && terrainRepository.heightMapTexture != null ? (TextureResourceRef) streamingService.stream(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE) : null;
        return mesh != null && heightMap != null;
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
    protected Shader getShader() {
        return shaderRepository.gBufferTerrainShader;
    }

    @Override
    protected void renderInternal() {
        prepareCall();

        shaderService.bindSampler2d(heightMap, heightMapU);
        shaderService.bindInt(heightMap.width, planeSize);
        shaderService.bindFloat(terrainRepository.heightScale, heightScale);
        shaderService.bindBoolean(true, fallbackMaterial);

        meshService.bind(mesh);
        meshService.setInstanceCount(0);
        meshService.draw();
    }

    @Override
    public String getTitle() {
        return "Terrain";
    }
}
