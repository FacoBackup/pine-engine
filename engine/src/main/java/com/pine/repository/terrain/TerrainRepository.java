package com.pine.repository.terrain;

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
import com.pine.theme.Icons;
import org.joml.Vector2f;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PBean
public class TerrainRepository extends Inspectable implements SerializableRepository {
    public String materialMask = UUID.randomUUID().toString();
    public String foliageMask = UUID.randomUUID().toString();
    public String heightMapTexture = UUID.randomUUID().toString();
    public TerrainChunk[] chunks = null;

    @PInject
    public transient ImporterService importer;

    @ExecutableField(label = Icons.calculate + "Import data")
    public void reGenFoliage() {
        new Thread(() -> {
            tryDelete(materialMask);
            tryDelete(foliageMask);
            tryDelete(heightMapTexture);

            int width = cellsX * quads;
            int height = cellsZ * quads;

            String foliageMaskLocal = UUID.randomUUID().toString();
            String heightMapTextureLocal = UUID.randomUUID().toString();
            String materialMaskLocal = UUID.randomUUID().toString();

            ImageUtil.generateTexture(width, height, importer.getPathToFile(foliageMaskLocal, StreamableResourceType.TEXTURE));
            ImageUtil.generateTexture(width, height, importer.getPathToFile(heightMapTextureLocal, StreamableResourceType.TEXTURE));
            ImageUtil.generateTexture(width, height, importer.getPathToFile(materialMaskLocal, StreamableResourceType.TEXTURE));

            if (heightMapTextureToImport != null) {
                var targetPath = importer.getPathToFile(heightMapTextureLocal, StreamableResourceType.TEXTURE);
                var originPath = importer.getPathToFile(heightMapTextureToImport, StreamableResourceType.TEXTURE);
                ImageUtil.copyInto(originPath, targetPath, 1);
            }

            var newChunks = new TerrainChunk[cellsX * cellsZ];
            int index = 0;
            for (int x = 0; x < cellsX; x++) {
                for (int z = 0; z < cellsZ; z++) {
                    float locationX = x * quads + offset.x;
                    float locationZ = z * quads + offset.y;
                    newChunks[index] = new TerrainChunk(locationX, locationZ, (float) Math.floor(locationX / quads), (float) Math.floor(locationZ / quads), x, z);
                    index++;
                }
            }
            foliageMask = foliageMaskLocal;
            heightMapTexture = heightMapTextureLocal;
            materialMask = materialMaskLocal;
            chunks = newChunks;
        }).start();
    }

    private void tryDelete(String id) {
        try {
            Files.delete(Paths.get(importer.getPathToFile(id, StreamableResourceType.TEXTURE)));
        } catch (Exception ignored) {
        }
    }

    @InspectableField(group = "Wind", label = "Frequency", min = 1)
    public float windFrequency = 20;

    @InspectableField(group = "Wind", label = "Strength", min = 0, max = 1)
    public float windStrength = .5f;

    @InspectableField(group = "Wind", label = "Amplitude", min = 0)
    public float windAmplitude = .15f;

    @InspectableField(label = "Render terrain")
    public boolean enabled = false;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Height map to import")
    public String heightMapTextureToImport;

    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    @InspectableField(group = "Terrain", label = "Casts shadows")
    public boolean castsShadows = true;

    @InspectableField(group = "Terrain", label = "Cells X axis", min = 1)
    public int cellsX = 2;

    @InspectableField(group = "Terrain", label = "Cells Z axis", min = 1)
    public int cellsZ = 2;

    @InspectableField(group = "Terrain", label = "Quads per cell (X by X)", min = 1)
    public int quads = 150;

    @InspectableField(group = "Terrain", label = "Height scale")
    public float heightScale = 1;

    @InspectableField(group = "Terrain", label = "Offset")
    public Vector2f offset = new Vector2f(0);

    public final Map<String, FoliageInstance> foliage = new HashMap<>();
    public final Map<String, MaterialInstance> materials = new HashMap<>();

    @Override
    public String getTitle() {
        return "Terrain & Foliage";
    }

    @Override
    public String getIcon() {
        return Icons.terrain;
    }
}
