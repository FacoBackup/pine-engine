package com.pine.service.svo;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.voxelization.VoxelizerRepository;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.tasks.SyncTask;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.Map;

@PBean
public class VoxelizerService implements SyncTask, Loggable {

    @PInject
    public VoxelizerRepository voxelizerRepository;

    @PInject
    public ResourceService resourceService;

    private boolean needsPackaging = true;
    private final Map<VoxelData, Integer> dataByIndex = new HashMap<>();
    private int currentVoxelDataMemIndex = 0;
    private int currentOctreeMemIndex = 0;

    @Override
    public void sync() {
        if (voxelizerRepository.sparseVoxelOctree == null || !needsPackaging) {
            return;
        }
        packageData();
        needsPackaging = false;
    }

    public void buildFromScratch() {
        voxelizerRepository.sparseVoxelOctree = new SparseVoxelOctree(voxelizerRepository.gridResolution, voxelizerRepository.maxDepth);

//        RenderingRequest request = renderingRepository.requests.getFirst();
//        MeshStreamData rawMeshData = meshService.stream(request.mesh);
//        traverseMesh(rawMeshData, request.transformation.globalMatrix);
//        voxelizerRepository.sparseVoxelOctree.insert(new Vector3f(1, 0, 5f), new VoxelData(1, 0, 0));
        long start = System.currentTimeMillis();
        float scale = .2f;
        int qtd = 1000;
        for (int j = 0; j < qtd; j++) {
            for (int k = 0; k < qtd; k++) {
                voxelizerRepository.sparseVoxelOctree.insert(new Vector3f(j * scale, 0, k * scale), new VoxelData(1, 0, 0));
            }
        }
        getLogger().warn("Took {}ms", System.currentTimeMillis() - start);
        needsPackaging = true;
    }

    private void packageData() {
        cleanStorage();
        putData(voxelizerRepository.sparseVoxelOctree.getRoot());
        fillStorage(voxelizerRepository.sparseVoxelOctree.getRoot());
        createStorage();
    }

    private void fillStorage(OctreeNode root) {
        // Generates uint for the voxel metadata based on its children's location on the buffer
        voxelizerRepository.octreeMemBuffer.put(root.getDataIndex(), root.packVoxelData(root.isLeaf() ? 0 : currentOctreeMemIndex / OctreeNode.INFO_PER_VOXEL));
        if (!root.isLeaf()) {
            for (var child : root.getChildren()) {
                if (child != null) {
                    putData(child);
                }
            }
            for (var child : root.getChildren()) {
                if (child != null) {
                    fillStorage(child);
                }
            }
        }
    }

    private void putData(OctreeNode root) {
        root.setDataIndex(currentOctreeMemIndex);
        voxelizerRepository.octreeMemBuffer.put(currentOctreeMemIndex, 0); // Placeholder for the actual voxel metadata
        currentOctreeMemIndex++;
        voxelizerRepository.octreeMemBuffer.put(currentOctreeMemIndex, root.getData().compress());
        currentOctreeMemIndex++;
    }

    private void cleanStorage() {
        currentOctreeMemIndex = 0;
        currentVoxelDataMemIndex = 0;
        if (voxelizerRepository.octreeSSBO != null) {
            resourceService.remove(voxelizerRepository.octreeSSBO.getId());
        }
        voxelizerRepository.octreeMemBuffer = MemoryUtil.memAllocInt(voxelizerRepository.sparseVoxelOctree.getNodeQuantity() * OctreeNode.INFO_PER_VOXEL);


        int sizeInBits = voxelizerRepository.sparseVoxelOctree.getNodeQuantity() * OctreeNode.INFO_PER_VOXEL * 32;
        getLogger().warn("Node quantity {}", voxelizerRepository.sparseVoxelOctree.getNodeQuantity());
        getLogger().warn("Buffer size {}bits {}bytes {}mb", sizeInBits, (sizeInBits / 8), (sizeInBits / 8) * 1e+6);

    }

    private void createStorage() {
        voxelizerRepository.octreeSSBO = (ShaderStorageBufferObject) resourceService.addResource(new SSBOCreationData(
                12,
                voxelizerRepository.octreeMemBuffer
        ));

        MemoryUtil.memFree(voxelizerRepository.octreeMemBuffer);

        voxelizerRepository.octreeMemBuffer = null;
        dataByIndex.clear();
        currentVoxelDataMemIndex = 0;
        currentOctreeMemIndex = 0;
    }
}
