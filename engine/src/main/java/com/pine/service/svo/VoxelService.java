package com.pine.service.svo;

import com.pine.FSUtil;
import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;
import com.pine.repository.VoxelRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.data.MaterialStreamData;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.impl.MaterialService;
import com.pine.service.streaming.impl.MeshService;
import com.pine.service.streaming.impl.TextureService;
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
public class VoxelService implements Loggable {

    @PInject
    public VoxelRepository voxelRepository;

    @PInject
    public ImporterService importerService;

    @PInject
    public WorldRepository worldRepository;

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

    public boolean buildFromScratch() {
        if (isVoxelizing) {
            return false;
        }
        isVoxelizing = true;
        if (voxelRepository.grid != null) {
            for (var chunk : voxelRepository.grid.chunks) {
                AbstractResourceRef<?> ref = streamingService.repository.streamableResources.get(chunk.getId());
                if (ref != null) {
                    ref.dispose();
                }
                streamingService.repository.failedStreams.put(chunk.getId(), StreamableResourceType.VOXEL_CHUNK);
                streamingService.repository.streamableResources.remove(chunk.getId());
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
            List<AbstractComponent> meshes = worldRepository.components.get(ComponentType.MESH);
            getLogger().warn("Voxelizing {}", meshes.size());
            for (AbstractComponent component : meshes) {
                var meshComponent = (MeshComponent) component;
                if (meshComponent.lod0 == null) {
                    continue;
                }

                getLogger().warn("Voxelizing entity {}", meshComponent.getEntity().name);
                Matrix4f globalMatrix = meshComponent.getEntity().transformation.globalMatrix;
                var mesh = streamMesh(meshComponent.lod0, globalMatrix, meshComponent);
                List<SparseVoxelOctree> intersectingChunks = getIntersectingChunks(mesh, grid, meshComponent);
                if (intersectingChunks.isEmpty()) {
                    getLogger().warn("No intersections found for {}", meshComponent.entity.name);
                    continue;
                }
                getLogger().warn("{} intersections found for {}", intersectingChunks.size(), meshComponent.entity.name);

                TextureStreamData albedoTexture = streamTexture(mesh, meshComponent);
                long startLocal = System.currentTimeMillis();
                for (SparseVoxelOctree chunk : intersectingChunks) {
                    VoxelizerUtil.voxelize(mesh, chunk, voxelRepository.voxelizationStepSize, albedoTexture);
                }
                getLogger().warn("Voxelization of {} took {}ms", meshComponent.lod0, System.currentTimeMillis() - startLocal);

                if (albedoTexture != null) {
                    STBImage.stbi_image_free(albedoTexture.imageBuffer);
                }
            }

            writeChunks(grid);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        isVoxelizing = false;
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
                if (!FSUtil.write(voxels, pathToFile)) {
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
