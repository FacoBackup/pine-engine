package com.pine.service.svo;

import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;
import com.pine.repository.core.CoreSSBORepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.voxelization.VoxelRepository;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.resource.SSBOService;
import com.pine.service.streaming.mesh.MeshService;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.tasks.SyncTask;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PBean
public class VoxelService implements SyncTask, Loggable {

    @PInject
    public VoxelRepository voxelRepository;

    @PInject
    public CoreSSBORepository coreSSBORepository;

    @PInject
    public SSBOService ssboService;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public MeshService meshService;

    private boolean needsPackaging = true;

    @Override
    public void sync() {
        if (voxelRepository.voxels == null || !needsPackaging) {
            return;
        }
        packageData();
        needsPackaging = false;
    }

    public void buildFromScratch() {
        new Thread(this::voxelize).start();
    }

    private void voxelize() {
        long startTotal = System.currentTimeMillis();
        Map<String, MeshImportData> byId = new HashMap<>();

        Map<Integer, List<MeshComponent>> meshGrid = new HashMap<>();
        SparseVoxelOctree octree = new SparseVoxelOctree(
                voxelRepository.gridScale,
                voxelRepository.maxDepth,
                voxelRepository.voxelizationStepSize
        );
        for (AbstractComponent component : worldRepository.components.get(ComponentType.MESH)) {
            long startLocal = System.currentTimeMillis();
            var mesh = (MeshComponent) component;
            String meshLOD = mesh.lod0;
            if (meshLOD != null) {
                Entity entity = mesh.getEntity();
                Vector3f absoluteTranslation = new Vector3f();
                entity.transformation.globalMatrix.getTranslation(absoluteTranslation);
                if (!byId.containsKey(meshLOD)) {
                    byId.put(meshLOD, (MeshImportData) meshService.stream(meshLOD));
                }

                VoxelizerUtil.traverseMesh(byId.get(meshLOD), entity.transformation.globalMatrix, octree);
                getLogger().warn("Voxelization of {} took {}", mesh.lod0, System.currentTimeMillis() - startLocal);
            }
        }


        long startMemory = System.currentTimeMillis();
        voxelRepository.voxels = octree.buildBuffer();
        getLogger().warn("Voxel buffer creation took {}ms", System.currentTimeMillis() - startMemory);

        needsPackaging = true;
        getLogger().warn("Total voxelization time {}ms", System.currentTimeMillis() - startTotal);
    }

    private void packageData() {
        int sizeInBits = voxelRepository.voxels.length * 32;
        getLogger().warn("Node quantity {}", voxelRepository.voxels.length);
        getLogger().warn("Buffer size {}bits {}bytes {}mb", sizeInBits, (sizeInBits / 8), sizeInBits / 8_000_000);

        var octreeMemBuffer = MemoryUtil.memAllocInt(voxelRepository.voxels.length);
        int[] voxels = voxelRepository.voxels;
        for (int i = 0, voxelsLength = voxels.length; i < voxelsLength; i++) {
            int voxelData = voxels[i];
            octreeMemBuffer.put(i, voxelData);
        }
        ssboService.updateBuffer(coreSSBORepository.octreeSSBO, octreeMemBuffer, 0);
        MemoryUtil.memFree(octreeMemBuffer);
    }
}
