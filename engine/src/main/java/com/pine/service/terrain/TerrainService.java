package com.pine.service.terrain;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.TerrainRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.HashGridService;
import com.pine.service.grid.Tile;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.impl.MeshImporter;
import com.pine.service.meshlet.TerrainGenerationUtil;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.impl.TextureService;
import com.pine.service.streaming.ref.TextureResourceRef;
import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;

import java.util.Collections;

import static com.pine.service.grid.HashGrid.TILE_SIZE;

@PBean
public class TerrainService implements Loggable {
    @PInject
    public MeshImporter meshImporter;

    @PInject
    public ImporterService importerService;

    @PInject
    public TextureService textureService;

    @PInject
    public TerrainRepository terrainRepository;

    @PInject
    public HashGridService hashGridService;

    @PInject
    public StreamingService streamingService;

    private boolean isBaking = false;

    public boolean bake() {
        if (isBaking) {
            return false;
        }
        isBaking = true;

        var t = new Thread(this::process);
        t.start();
        return true;
    }

    private void process() {
        try {
            if (terrainRepository.bakeId != null) {
                FSUtil.delete(importerService.getPathToFile(terrainRepository.bakeId, StreamableResourceType.MESH));
            }
            hashGridService.getTiles().values().forEach(t -> t.isTerrainPresent = false);

            long start = System.currentTimeMillis();
            String imagePath = importerService.getPathToFile(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE);

            var texture = (TextureStreamData) textureService.stream(imagePath, Collections.emptyMap(), Collections.emptyMap());
            STBImage.stbi_image_free(texture.imageBuffer);

            var mesh = TerrainGenerationUtil.computeMesh(TILE_SIZE);
            mesh.id = terrainRepository.id;
            meshImporter.persist(mesh);

            ImageUtil.splitImage(imagePath, importerService.engine.getResourceDirectory(), this::writeTile);
            streamingService.repository.discardedResources.clear();
            getLogger().warn("Terrain processing took {}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            getLogger().error("Could not process Terrain", e);
        }
    }

    private void writeTile(Integer width, Integer x, Integer z) {
        String id = ImageUtil.getTerrainTileName(x, z);

        var tile = hashGridService.getHashGrid().getOrCreateTile(new Vector3f(x * TILE_SIZE, 0, z * TILE_SIZE));
        tile.isTerrainPresent = true;
        tile.terrainFoliageId = id + Tile.FOLIAGE_MASK;
        tile.terrainHeightMapId = id;

        // MASK TEXTURE
        ImageUtil.generateTexture(width, width, importerService.engine.getResourceDirectory() + tile.terrainFoliageId + "." + StreamableResourceType.TEXTURE.name());
    }

    public void onSave() {
        for (var tile : hashGridService.getLoadedTiles()) {
            if (tile != null && tile.isTerrainPresent) {
                var mask = (TextureResourceRef) streamingService.streamIn(tile.getId(), StreamableResourceType.TEXTURE);
                if (mask != null && mask.isLoaded()) {
                    textureService.writeTexture(importerService.getPathToFile(mask.id, StreamableResourceType.TEXTURE), mask.width, mask.height, mask.texture);
                }

                var heightMap = (TextureResourceRef) streamingService.streamIn(tile.getId(), StreamableResourceType.TEXTURE);
                if (heightMap != null && heightMap.isLoaded()) {
                    textureService.writeTexture(importerService.getPathToFile(heightMap.id, StreamableResourceType.TEXTURE), heightMap.width, heightMap.height, heightMap.texture);
                }
            }
        }
    }
}
