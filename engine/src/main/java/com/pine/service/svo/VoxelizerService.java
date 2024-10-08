package com.pine.service.svo;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.voxelization.VoxelizerRepository;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.service.streaming.mesh.MeshService;
import com.pine.tasks.SyncTask;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

@PBean
public class VoxelizerService implements SyncTask, Loggable {

    private static final int INFO_PER_VOXEL = 4;
    private static final int INFO_PER_VOXEL_DATA = 4;

    @PInject
    public MeshService meshService;

    @PInject
    public VoxelizerRepository voxelizerRepository;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public ResourceService resourceService;

    private boolean needsPackaging = true;
    private int currentOctreeMemIndex = 0;
    private int currentVoxelDataMemIndex = 0;

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
        voxelizerRepository.sparseVoxelOctree.insert(new Vector3f(1, 0, .5f), new VoxelData(1, 0, 0));
        needsPackaging = true;
    }

    private void packageData() {
        cleanStorage();
        fillStorage(voxelizerRepository.sparseVoxelOctree.getRoot());
        createStorage();
    }

    private void fillStorage(OctreeNode root) {
        voxelizerRepository.octreeMemBuffer.put(currentOctreeMemIndex, root.getChildMask());
        currentOctreeMemIndex++;
        voxelizerRepository.octreeMemBuffer.put(currentOctreeMemIndex, currentVoxelDataMemIndex);
        currentOctreeMemIndex++;

        voxelizerRepository.voxelDataMemBuffer.put(currentVoxelDataMemIndex, root.getData().r());
        voxelizerRepository.voxelDataMemBuffer.put(currentVoxelDataMemIndex + 1, root.getData().g());
        voxelizerRepository.voxelDataMemBuffer.put(currentVoxelDataMemIndex + 2, root.getData().b());
        currentVoxelDataMemIndex += 3;
    }

    private void cleanStorage() {
        currentOctreeMemIndex = 0;
        currentVoxelDataMemIndex = 0;
        if (voxelizerRepository.octreeSSBO != null) {
            resourceService.remove(voxelizerRepository.octreeSSBO.getId());
            resourceService.remove(voxelizerRepository.voxelDataSSBO.getId());
        }
        voxelizerRepository.octreeMemBuffer = MemoryUtil.memAllocInt(voxelizerRepository.sparseVoxelOctree.getNodeQuantity() * OctreeNode.INFO_PER_VOXEL);
        voxelizerRepository.voxelDataMemBuffer = MemoryUtil.memAllocFloat(voxelizerRepository.sparseVoxelOctree.getNodeQuantity() * VoxelData.INFO_PER_VOXEL);
    }

    private void createStorage() {
        voxelizerRepository.octreeSSBO = (ShaderStorageBufferObject) resourceService.addResource(new SSBOCreationData(
                12,
                voxelizerRepository.octreeMemBuffer
        ));

        voxelizerRepository.voxelDataSSBO = (ShaderStorageBufferObject) resourceService.addResource(new SSBOCreationData(
                13,
                voxelizerRepository.voxelDataMemBuffer
        ));

        MemoryUtil.memFree(voxelizerRepository.octreeMemBuffer);
        MemoryUtil.memFree(voxelizerRepository.voxelDataMemBuffer);

        voxelizerRepository.octreeMemBuffer = null;
        voxelizerRepository.voxelDataMemBuffer = null;
    }
}
