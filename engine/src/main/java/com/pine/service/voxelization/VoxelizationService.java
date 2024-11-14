package com.pine.service.voxelization;

import com.pine.FSUtil;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.VoxelRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.HashGridService;
import com.pine.service.grid.Tile;
import com.pine.service.grid.TileWorld;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.data.MaterialStreamData;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.impl.MaterialService;
import com.pine.service.streaming.impl.MeshService;
import com.pine.service.streaming.impl.TextureService;
import com.pine.service.voxelization.svo.SparseVoxelOctree;
import com.pine.service.voxelization.util.MeshUtil;
import com.pine.service.voxelization.util.VoxelizerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;

import java.util.*;

import static com.pine.service.grid.HashGrid.TILE_SIZE;

@PBean
public class VoxelizationService implements Loggable {

    @PInject
    public VoxelRepository voxelRepository;

    @PInject
    public ImporterService importerService;

    @PInject
    public MeshService meshService;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public TextureService textureService;

    @PInject
    public HashGridService hashGridService;

    @PInject
    public MaterialService materialService;

    private boolean isVoxelizing;

    public boolean bake() {
        if (isVoxelizing) {
            return false;
        }
        isVoxelizing = true;
        new Thread(this::voxelize).start();
        return true;
    }

    private void voxelize() {
        try {
            for (var tile : hashGridService.getTiles().values()) {
                long start = System.currentTimeMillis();
                var world = tile.getWorld();
                createSvo(tile);
                for (MeshComponent meshComponent : world.bagMeshComponent.values()) {
                    voxelizeMesh(meshComponent, world, tile);
                }
                getLogger().warn("Tile voxelization took {}ms", System.currentTimeMillis() - start);
            }

            for (var tile : hashGridService.getTiles().values()) {
                writeTileSvo(tile);
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        isVoxelizing = false;
    }

    private void voxelizeMesh(MeshComponent meshComponent, TileWorld world, Tile tile) {
        if (meshComponent.lod0 == null) {
            return;
        }

        getLogger().warn("Voxelizing entity {}", meshComponent.getEntityId());
        Matrix4f globalMatrix = world.bagTransformationComponent.get(meshComponent.getEntityId()).modelMatrix;
        var mesh = streamMesh(meshComponent.lod0, globalMatrix, meshComponent);
        TextureStreamData albedoTexture = streamTexture(mesh, meshComponent);
        long startLocal = System.currentTimeMillis();
        VoxelizerUtil.voxelize(mesh, tile.getSvo(), albedoTexture);
        processAdjacentTiles(tile, mesh, albedoTexture);
        getLogger().warn("Voxelization of {} took {}ms", meshComponent.lod0, System.currentTimeMillis() - startLocal);

        if (albedoTexture != null) {
            STBImage.stbi_image_free(albedoTexture.imageBuffer);
        }
    }

    /**
     * PROCESS ADJACENT TILES SINCE SOME MESHES CAN OVERFLOW THE CURRENT TILE INTO THE ADJACENT ONES
     * @param tile current tile
     * @param mesh current mesh
     * @param albedoTexture albedo texture data
     */
    private void processAdjacentTiles(Tile tile, MeshImportData mesh, TextureStreamData albedoTexture) {
        for (String adjacentTile : tile.getAdjacentTiles()) {
            if (adjacentTile != null && hashGridService.getTiles().containsKey(adjacentTile)) {
                var tileLocal = hashGridService.getTiles().get(adjacentTile);
                createSvo(tileLocal);
                VoxelizerUtil.voxelize(mesh, tileLocal.getSvo(), albedoTexture);
            }
        }
    }

    private void createSvo(Tile tile) {
        if (tile.getSvo() == null || tile.getSvo().getDepth() != voxelRepository.maxDepth || tile.getSvo().getSize() != voxelRepository.chunkGridSize) {
            tile.setSvo(new SparseVoxelOctree(tile.getBoundingBox(), voxelRepository.chunkGridSize, voxelRepository.maxDepth));
        }
    }

    private @Nullable TextureStreamData streamTexture(MeshImportData mesh, MeshComponent meshComponent) {
        TextureStreamData albedoTexture = null;
        try {
            if (mesh.uvs != null && meshComponent.material != null) {
                var material = (MaterialStreamData) materialService.stream(importerService.getPathToFile(meshComponent.material, StreamableResourceType.MATERIAL), new HashMap<>(), new HashMap<>());
                if (material.albedo != null) {
                    albedoTexture = (TextureStreamData) textureService.stream(importerService.getPathToFile(material.albedo.id, StreamableResourceType.TEXTURE), Collections.emptyMap(), Collections.emptyMap());
                }
            }
        } catch (Exception e) {
            getLogger().error("Could not voxelize mesh", e);
        }
        return albedoTexture;
    }

    private @NotNull MeshImportData streamMesh(String meshLOD, Matrix4f globalMatrix, MeshComponent meshComponent) {
        long startLocal = System.currentTimeMillis();
        var mesh = MeshUtil.transformVertices((MeshImportData) meshService.stream(importerService.getPathToFile(meshLOD, StreamableResourceType.MESH), Collections.emptyMap(), Collections.emptyMap()), globalMatrix);
        getLogger().warn("Streaming of {} took {}ms", meshComponent.lod0, System.currentTimeMillis() - startLocal);
        return mesh;
    }

    private void writeTileSvo(Tile tile) {
        long startMemory = System.currentTimeMillis();
        try {
            int[] voxels = tile.getSvo().buildBuffer();
            tile.getSvo().purgeData();
            String pathToFile = importerService.getPathToFile(tile.getId(), StreamableResourceType.VOXEL_CHUNK);
            if (!FSUtil.writeBinary(voxels, pathToFile)) {
                getLogger().error("Could not write chunk to disk");
            }
        } catch (Exception e) {
            getLogger().error("Could not write chunk to disk", e);
        }
        getLogger().warn("Writing voxels for tile {} took {}ms", tile.getId(), System.currentTimeMillis() - startMemory);
    }
}
