package com.pine.service.system.impl;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;
import org.joml.Vector2f;

import static com.pine.service.grid.HashGrid.TILE_SIZE;

public class TerrainGBufferPass extends AbstractGBufferPass {
    private final Vector2f terrainLocation = new Vector2f();
    private UniformDTO planeSize;
    private UniformDTO heightScale;
    private UniformDTO terrainLocationU;
    private UniformDTO fallbackMaterial;

    @Override
    public void onInitialize() {
        super.onInitialize();

        planeSize = addUniformDeclaration("planeSize");
        heightScale = addUniformDeclaration("heightScale");
        terrainLocationU = addUniformDeclaration("terrainLocation");
        fallbackMaterial = addUniformDeclaration("fallbackMaterial");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferTerrainShader;
    }

    @Override
    protected void renderInternal() {
        boolean isPrepared = false;
        for (var tile : hashGridService.getLoadedTiles()) {
            if (tile != null && tile.isTerrainPresent) {
                if (!isPrepared) {
                    prepareCall();
                    isPrepared = true;
                }

                var foliageMask = (TextureResourceRef) streamingService.streamIn(tile.terrainFoliageId, StreamableResourceType.TEXTURE);
                var heightMap = (TextureResourceRef) streamingService.streamIn(tile.terrainHeightMapId, StreamableResourceType.TEXTURE);
                var mesh = (MeshResourceRef) streamingService.streamIn(terrainRepository.id, StreamableResourceType.MESH);
                if (mesh != null && heightMap != null && foliageMask != null) {
                    foliageMask.lastUse = mesh.lastUse = heightMap.lastUse = clockRepository.totalTime;
                    renderChunk(mesh, heightMap, tile.getX(), tile.getZ());
                }
            }
        }
    }

    private void renderChunk(MeshResourceRef mesh, TextureResourceRef heightMap, int x, int z) {
        shaderService.bindSampler2dDirect(heightMap, 8);
        shaderService.bindInt(heightMap.width, planeSize);

        terrainLocation.x = x * TILE_SIZE + TILE_SIZE / 2f;
        terrainLocation.y = z * TILE_SIZE + TILE_SIZE / 2f;
        shaderService.bindVec2(terrainLocation, terrainLocationU);
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
