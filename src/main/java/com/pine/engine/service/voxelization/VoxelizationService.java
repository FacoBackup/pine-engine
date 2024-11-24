package com.pine.engine.service.voxelization;

import com.pine.FSUtil;
import com.pine.engine.component.MeshComponent;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.engine.repository.VoxelRepository;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.repository.rendering.RenderingRepository;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.voxelization.svo.SparseVoxelOctree;
import com.pine.engine.service.voxelization.util.MeshUtil;
import com.pine.engine.service.world.WorldService;
import com.pine.engine.service.world.WorldTile;
import com.pine.engine.service.importer.ImporterService;
import com.pine.engine.service.importer.data.MeshImportData;
import com.pine.engine.service.streaming.StreamingService;
import com.pine.engine.service.streaming.data.MaterialStreamData;
import com.pine.engine.service.streaming.data.TextureStreamData;
import com.pine.engine.service.streaming.impl.MaterialService;
import com.pine.engine.service.streaming.impl.MeshService;
import com.pine.engine.service.streaming.impl.TextureService;
import com.pine.engine.service.voxelization.util.VoxelizerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.stb.STBImage;

import java.util.Collections;
import java.util.HashMap;

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
    public WorldService worldService;

    @PInject
    public MaterialService materialService;

    @PInject
    public WorldRepository world;

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
            for (var tile : worldService.getTiles().values()) {
                long start = System.currentTimeMillis();
                for(var entity : tile.getEntities()){
                    var meshComponent = world.bagMeshComponent.get(entity);
                    if(meshComponent != null){
                        createSvo(tile);
                        voxelizeMesh(meshComponent, tile);
                    }
                }
                getLogger().warn("Tile voxelization took {}ms", System.currentTimeMillis() - start);
            }

            for (var tile : worldService.getTiles().values()) {
                writeTileSvo(tile);
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        isVoxelizing = false;
    }

    private void voxelizeMesh(MeshComponent meshComponent, WorldTile worldTile) {
        if (meshComponent.lod0 == null) {
            return;
        }

        getLogger().warn("Voxelizing entity {}", meshComponent.getEntityId());
        Matrix4f globalMatrix = world.bagTransformationComponent.get(meshComponent.getEntityId()).modelMatrix;
        var mesh = streamMesh(meshComponent.lod0, globalMatrix, meshComponent);
        TextureStreamData albedoTexture = streamTexture(mesh, meshComponent);
        long startLocal = System.currentTimeMillis();
        VoxelizerUtil.voxelize(mesh, worldTile.getSvo(), albedoTexture);
        processAdjacentTiles(worldTile, mesh, albedoTexture);
        getLogger().warn("Voxelization of {} took {}ms", meshComponent.lod0, System.currentTimeMillis() - startLocal);

        if (albedoTexture != null) {
            STBImage.stbi_image_free(albedoTexture.imageBuffer);
        }
    }

    /**
     * PROCESS ADJACENT TILES SINCE SOME MESHES CAN OVERFLOW THE CURRENT TILE INTO THE ADJACENT ONES
     * @param worldTile current tile
     * @param mesh current mesh
     * @param albedoTexture albedo texture data
     */
    private void processAdjacentTiles(WorldTile worldTile, MeshImportData mesh, TextureStreamData albedoTexture) {
        for (String adjacentTile : worldTile.getAdjacentTiles()) {
            if (adjacentTile != null && worldService.getTiles().containsKey(adjacentTile)) {
                var tileLocal = worldService.getTiles().get(adjacentTile);
                createSvo(tileLocal);
                VoxelizerUtil.voxelize(mesh, tileLocal.getSvo(), albedoTexture);
            }
        }
    }

    private void createSvo(WorldTile worldTile) {
        if (worldTile.getSvo() == null || worldTile.getSvo().getDepth() != voxelRepository.maxDepth || worldTile.getSvo().getSize() != voxelRepository.chunkGridSize) {
            worldTile.setSvo(new SparseVoxelOctree(worldTile.getBoundingBox(), voxelRepository.chunkGridSize, voxelRepository.maxDepth));
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

    private void writeTileSvo(WorldTile worldTile) {
        long startMemory = System.currentTimeMillis();
        try {
            int[] voxels = worldTile.getSvo().buildBuffer();
            worldTile.getSvo().purgeData();
            String pathToFile = importerService.getPathToFile(worldTile.getId(), StreamableResourceType.VOXEL_CHUNK);
            if (!FSUtil.writeBinary(voxels, pathToFile)) {
                getLogger().error("Could not write chunk to disk");
            }
        } catch (Exception e) {
            getLogger().error("Could not write chunk to disk", e);
        }
        getLogger().warn("Writing voxels for tile {} took {}ms", worldTile.getId(), System.currentTimeMillis() - startMemory);
    }
}
