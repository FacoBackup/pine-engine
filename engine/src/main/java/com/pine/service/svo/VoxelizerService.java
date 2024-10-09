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
        voxelizerRepository.sparseVoxelOctree.insert(new Vector3f(-5), new VoxelData(1, 0, 0));
        voxelizerRepository.sparseVoxelOctree.insert(new Vector3f(10), new VoxelData(0, 1, 0));
        voxelizerRepository.sparseVoxelOctree.insert(new Vector3f(2), new VoxelData(0, 0, 1));
        voxelizerRepository.sparseVoxelOctree.insert(new Vector3f(5), new VoxelData(.5f, 0, 1));
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
