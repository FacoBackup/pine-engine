package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.inspection.ExecutableField;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.ImageUtil;
import com.pine.service.importer.ImporterService;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.impl.TextureService;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.theme.Icons;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PBean
public class TerrainRepository extends Inspectable implements SerializableRepository {
    public String foliageMask = UUID.randomUUID().toString();
    public String heightMapTexture = UUID.randomUUID().toString();

    @PInject
    public transient ImporterService importer;

    @ExecutableField(label = Icons.calculate + "Import data")
    public void reGenFoliage() {
        new Thread(() -> {
            tryDelete(foliageMask);
            tryDelete(heightMapTexture);

            int width = cellsX * quads;
            int height = cellsZ * quads;

            String foliageMaskLocal = UUID.randomUUID().toString();
            String heightMapTextureLocal = UUID.randomUUID().toString();

            ImageUtil.generateTexture(width, height, importer.getPathToFile(foliageMaskLocal, StreamableResourceType.TEXTURE));
            ImageUtil.generateTexture(width, height, importer.getPathToFile(heightMapTextureLocal, StreamableResourceType.TEXTURE));
            if (heightMapTextureToImport != null) {
                var targetPath = importer.getPathToFile(heightMapTextureLocal, StreamableResourceType.TEXTURE);
                var originPath = importer.getPathToFile(heightMapTextureToImport, StreamableResourceType.TEXTURE);
                ImageUtil.copyInto(originPath, targetPath, 1);
            }

            foliageMask = foliageMaskLocal;
            heightMapTexture = heightMapTextureLocal;
            heightMapTextureToImport = null;
        }).start();
    }

    private void tryDelete(String id) {
        try {
            Files.delete(Paths.get(importer.getPathToFile(id, StreamableResourceType.TEXTURE)));
        } catch (Exception ignored) {
        }
    }

    @InspectableField(label = "Render terrain")
    public boolean enabled = false;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Height map to import")
    public String heightMapTextureToImport;

    @InspectableField(label = "Casts shadows")
    public boolean castsShadows = true;

    @InspectableField(label = "Cells X axis", min = 1)
    public int cellsX = 2;

    @InspectableField(label = "Cells Z axis", min = 1)
    public int cellsZ = 2;

    @InspectableField(label = "Quads per cell (X by X)", min = 1)
    public int quads = 150;

    @InspectableField(label = "Height scale")
    public float heightScale = 1;

    @InspectableField(label = "Offset X")
    public int offsetX = 10;

    @InspectableField(label = "Offset Z")
    public int offsetZ = 10;

    public final Map<String, FoliageInstance> foliage = new HashMap<>();

    @PInject
    public transient StreamingService streamingService;

    @PInject
    public transient ImporterService importerService;

    @PInject
    public transient TextureService textureService;

    @Override
    public void onSave() {
        var mask = (TextureResourceRef) streamingService.streamIn(foliageMask, StreamableResourceType.TEXTURE);
        if (mask != null && mask.isLoaded()) {
            textureService.writeTexture(importerService.getPathToFile(mask.id, StreamableResourceType.TEXTURE), mask.width, mask.height, mask.texture);
        }

        var heightMap = (TextureResourceRef) streamingService.streamIn(heightMapTexture, StreamableResourceType.TEXTURE);
        if (heightMap != null && heightMap.isLoaded()) {
            textureService.writeTexture(importerService.getPathToFile(heightMap.id, StreamableResourceType.TEXTURE), heightMap.width, heightMap.height, heightMap.texture);
        }
    }

    @Override
    public String getTitle() {
        return "Terrain";
    }

    @Override
    public String getIcon() {
        return Icons.terrain;
    }
}
