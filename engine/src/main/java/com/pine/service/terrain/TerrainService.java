package com.pine.service.terrain;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.TerrainRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.impl.MeshImporter;
import com.pine.service.meshlet.TerrainGenerationUtil;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.impl.TextureService;
import org.lwjgl.stb.STBImage;

import java.util.Collections;

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

            long start = System.currentTimeMillis();

            var texture = (TextureStreamData) textureService.stream(importerService.getPathToFile(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE), Collections.emptyMap(), Collections.emptyMap());
            STBImage.stbi_image_free(texture.imageBuffer);

            var mesh = TerrainGenerationUtil.computeMesh(texture.width);
            meshImporter.persist(mesh);

            terrainRepository.bakeId = mesh.id;

            getLogger().warn("Terrain processing took {}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            getLogger().error("Could not process Terrain", e);
        }
        isBaking = false;
    }
}
