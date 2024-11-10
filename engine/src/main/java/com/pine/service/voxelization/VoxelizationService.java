package com.pine.service.voxelization;

import com.pine.FSUtil;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.VoxelRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.HashGridService;
import com.pine.service.grid.TileWorld;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.data.MaterialStreamData;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.impl.MaterialService;
import com.pine.service.streaming.impl.MeshService;
import com.pine.service.streaming.impl.TextureService;
import com.pine.service.voxelization.svo.SVOGrid;
import com.pine.service.voxelization.svo.SparseVoxelOctree;
import com.pine.service.voxelization.util.MeshUtil;
import com.pine.service.voxelization.util.VoxelizerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@PBean
public class VoxelizationService implements Loggable {

    @PInject
    public VoxelRepository voxelRepository;

    @PInject
    public ImporterService importerService;

    @PInject
    public HashGridService hashGridService;

    @PInject
    public MeshService meshService;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public TextureService textureService;

    @PInject
    public MaterialService materialService;

    private boolean isVoxelizing;

    public boolean bake() {
        if (isVoxelizing) {
            return false;
        }
        isVoxelizing = true;
        if (voxelRepository.grid != null) {
            for (var chunk : voxelRepository.grid.chunks) {
                AbstractResourceRef<?> ref = streamingService.repository.streamed.get(chunk.getId());
                if (ref != null) {
                    ref.dispose();
                }
                streamingService.repository.discardedResources.put(chunk.getId(), StreamableResourceType.VOXEL_CHUNK);
                streamingService.repository.streamed.remove(chunk.getId());
            }
        }
        new Thread(this::voxelize).start();
        return true;
    }

    private void voxelize() {
        try {
            if (voxelRepository.grid != null) {
                for (var chunk : voxelRepository.grid.chunks) {
                    try {
                        new File(importerService.getPathToFile(chunk.getId(), StreamableResourceType.VOXEL_CHUNK)).delete();
                    } catch (Exception e) {
                        getLogger().error("Could not delete chunk {}", chunk.getId(), e);
                    }
                }
            }
            var grid = new SVOGrid(voxelRepository.chunkSize, voxelRepository.chunkGridSize, voxelRepository.maxDepth);

            for (var tile : hashGridService.getTiles().values()) {
                var world = tile.getWorld();
                getLogger().warn("Voxelizing {}", world.bagMeshComponent.size());
                for (MeshComponent meshComponent : world.bagMeshComponent.values()) {
                    voxelizeMesh(meshComponent, world, grid);
                }
            }

            writeChunks(grid);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        isVoxelizing = false;
    }

    private void voxelizeMesh(MeshComponent meshComponent, TileWorld world, SVOGrid grid) {
        if (meshComponent.lod0 == null) {
            return;
        }

        getLogger().warn("Voxelizing entity {}", meshComponent.getEntityId());
        Matrix4f globalMatrix = world.bagTransformationComponent.get(meshComponent.getEntityId()).modelMatrix;
        var mesh = streamMesh(meshComponent.lod0, globalMatrix, meshComponent);
        List<SparseVoxelOctree> intersectingChunks = getIntersectingChunks(mesh, grid, meshComponent);
        if (intersectingChunks.isEmpty()) {
            getLogger().warn("No intersections found for {}", meshComponent.getEntityId());
            return;
        }
        getLogger().warn("{} intersections found for {}", intersectingChunks.size(), meshComponent.getEntityId());

        TextureStreamData albedoTexture = streamTexture(mesh, meshComponent);
        long startLocal = System.currentTimeMillis();
        for (SparseVoxelOctree chunk : intersectingChunks) {
            VoxelizerUtil.voxelize(mesh, chunk, albedoTexture);
        }
        getLogger().warn("Voxelization of {} took {}ms", meshComponent.lod0, System.currentTimeMillis() - startLocal);

        if (albedoTexture != null) {
            STBImage.stbi_image_free(albedoTexture.imageBuffer);
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

    private List<SparseVoxelOctree> getIntersectingChunks(MeshImportData mesh, SVOGrid grid, MeshComponent meshComponent) {
        long startLocal = System.currentTimeMillis();
        var bb = MeshUtil.computeBoundingBox(mesh);
        List<SparseVoxelOctree> intersectingChunks = grid.getIntersectingChunks(bb);
        getLogger().warn("Bounding box computation of {} took {}ms", meshComponent.lod0, System.currentTimeMillis() - startLocal);
        return intersectingChunks;
    }

    private void writeChunks(SVOGrid grid) {
        long startMemory = System.currentTimeMillis();
        List<SparseVoxelOctree> toRemove = new ArrayList<>();
        for (var chunk : grid.chunks) {
            try {
                int[] voxels = chunk.buildBuffer();
                chunk.purgeData();

                if (voxels.length == 1 || voxels[0] == 0) {
                    toRemove.add(chunk);
                    continue;
                }

                String pathToFile = importerService.getPathToFile(chunk.getId(), StreamableResourceType.VOXEL_CHUNK);
                if (!FSUtil.writeBinary(voxels, pathToFile)) {
                    getLogger().error("Could not write chunk to disk");
                    toRemove.add(chunk);
                }
            } catch (Exception e) {
                getLogger().error("Could not write chunk to disk", e);
            }
        }

        toRemove.forEach(grid.chunks::remove);
        voxelRepository.grid = grid;
        getLogger().warn("Writing voxels took {}ms", System.currentTimeMillis() - startMemory);
    }

    public int getVoxelCount() {
        int total = 0;
        for (var chunk : renderingRepository.voxelChunks) {
            if (chunk != null) {
                total += chunk.getQuantity();
            }
        }
        return total;
    }
}
