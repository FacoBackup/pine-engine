package com.pine.service.svo;

import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;
import com.pine.repository.core.CoreSSBORepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.VoxelRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.resource.SSBOService;
import com.pine.service.streaming.impl.MeshService;
import com.pine.tasks.SyncTask;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

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

    public void buildFromScratch() {
        new Thread(this::voxelize).start();
    }

    private void voxelize() {
        if(voxelRepository.grid != null){
            for(var chunk : voxelRepository.grid.chunks){
                try{
                    new File(importerService.getPathToFile(chunk.getId(), StreamableResourceType.VOXEL_CHUNK)).delete();
                }catch (Exception e){
                    getLogger().error("Could not delete chunk {}", chunk.getId(), e);
                }
            }
        }
        var grid = new SVOGrid(voxelRepository.chunkSize, voxelRepository.chunkGridSize, voxelRepository.maxDepth);
        for (AbstractComponent component : worldRepository.components.get(ComponentType.MESH)) {
            var meshComponent = (MeshComponent) component;
            getLogger().warn("Voxelizing entity {}", meshComponent.getEntity().name);
            String meshLOD = meshComponent.lod0;
            if (meshLOD != null) {
                Matrix4f globalMatrix = meshComponent.getEntity().transformation.globalMatrix;

                long startLocal = System.currentTimeMillis();
                var mesh = MeshUtil.transformVertices((MeshImportData) meshService.stream(importerService.getPathToFile(meshLOD, StreamableResourceType.MESH)), globalMatrix);
                getLogger().warn("Streaming of {} took {}ms", meshComponent.lod0, System.currentTimeMillis() - startLocal);

                startLocal = System.currentTimeMillis();
                var bb = MeshUtil.computeBoundingBox(mesh);
                List<SparseVoxelOctree> intersectingChunks = grid.getIntersectingChunks(bb);
                getLogger().warn("Bounding box computation of {} took {}ms", meshComponent.lod0, System.currentTimeMillis() - startLocal);

                for (SparseVoxelOctree chunk : intersectingChunks) {
                    startLocal = System.currentTimeMillis();
                    VoxelizerUtil.traverseMesh(mesh, chunk, voxelRepository.voxelizationStepSize);
                    getLogger().warn("Voxelization of {} took {}", meshComponent.lod0, System.currentTimeMillis() - startLocal);
                }
            }
        }

        writeChunks(grid);
    }

    private void writeChunks(SVOGrid grid) {
        long startMemory = System.currentTimeMillis();
        for (var chunk : grid.chunks) {
            int[] voxels = chunk.buildBuffer();
            chunk.purgeData();

            String pathToFile = importerService.getPathToFile(chunk.getId(), StreamableResourceType.VOXEL_CHUNK);
            try {
                try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(pathToFile))) {
                    dos.writeInt(voxels.length);
                    for (int value : voxels) {
                        dos.writeInt(value);
                    }
                }
            } catch (Exception e) {
                getLogger().error("Could not write chunk to disk", e);
                grid.chunks.remove(chunk);
            }
        }
        voxelRepository.grid = grid;
        getLogger().warn("Writing voxels took {}ms", System.currentTimeMillis() - startMemory);
    }
}
