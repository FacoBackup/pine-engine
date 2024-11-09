package com.pine.service.system.impl;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;

public class TerrainGBufferPass extends AbstractGBufferPass {
    private MeshResourceRef mesh;
    private TextureResourceRef heightMap;
    private UniformDTO planeSize;
    private UniformDTO heightScale;
    private UniformDTO fallbackMaterial;

    @Override
    public void onInitialize() {
        super.onInitialize();

        planeSize = addUniformDeclaration("planeSize");
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
    protected Shader getShader() {
        return shaderRepository.gBufferTerrainShader;
    }

    @Override
    protected void renderInternal() {
        prepareCall();

        shaderService.bindSampler2dDirect(heightMap, 8);
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
