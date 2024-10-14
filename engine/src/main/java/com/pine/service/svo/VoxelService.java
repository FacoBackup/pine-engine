package com.pine.service.svo;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.core.CoreSSBORepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.voxelization.VoxelRepository;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.mesh.MeshService;
import com.pine.tasks.SyncTask;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

@PBean
public class VoxelService implements SyncTask, Loggable {

    @PInject
    public VoxelRepository voxelRepository;

    @PInject
    public CoreSSBORepository coreSSBORepository;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public MeshService meshService;

    private boolean needsPackaging = true;
    private final Map<VoxelData, Integer> dataByIndex = new HashMap<>();
    private int currentOctreeMemIndex = 0;

    @Override
    public void sync() {
        if (voxelRepository.voxels == null || !needsPackaging) {
            return;
        }
        packageData();
        needsPackaging = false;
    }

    public void buildFromScratch() {
        dataByIndex.clear();
        currentOctreeMemIndex = 0;
        long startTotal = System.currentTimeMillis();
        SparseVoxelOctree octree = new SparseVoxelOctree(
                voxelRepository.gridResolution,
                voxelRepository.maxDepth,
                voxelRepository.voxelizationStepSize
        );
        for (var request : renderingRepository.requests) {
            long startLocal = System.currentTimeMillis();
            VoxelizerUtil.traverseMesh(
                    meshService.stream(request.mesh.pathToFile),
                    request.transformation.localMatrix,
                    octree
            );
            getLogger().warn("Voxelization of {} took {}", request.mesh.name, System.currentTimeMillis() - startLocal);
        }

        long startMemory = System.currentTimeMillis();
        voxelRepository.voxels = new int[octree.getNodeQuantity() * OctreeNode.INFO_PER_VOXEL];
        OctreeNode rootNode = octree.getRoot();
        putData(rootNode);
        fillStorage(rootNode);
        getLogger().warn("Voxel buffer creation took {}ms", System.currentTimeMillis() - startMemory);

        needsPackaging = true;
        getLogger().warn("Total voxelization time {}ms", System.currentTimeMillis() - startTotal);
    }


    private void fillStorage(OctreeNode node) {
        // Generates uint for the voxel metadata based on its children's location on the buffer
        voxelRepository.voxels[node.getDataIndex()] = node.packVoxelData(node.isLeaf() ? 0 : currentOctreeMemIndex / OctreeNode.INFO_PER_VOXEL);
        if (!node.isLeaf()) {
            for (var child : node.getChildren()) {
                if (child != null) {
                    putData(child);
                }
            }
            for (var child : node.getChildren()) {
                if (child != null) {
                    fillStorage(child);
                }
            }
        }
    }

    private void putData(OctreeNode node) {
        node.setDataIndex(currentOctreeMemIndex);
        voxelRepository.voxels[currentOctreeMemIndex] = 0; // Placeholder for the actual voxel metadata
        currentOctreeMemIndex++;
        voxelRepository.voxels[currentOctreeMemIndex] = node.getData().compress();
        currentOctreeMemIndex++;
    }

    private void packageData() {
        int sizeInBits = voxelRepository.voxels.length * 32;
        getLogger().warn("Node quantity {}", voxelRepository.voxels.length / OctreeNode.INFO_PER_VOXEL);
        getLogger().warn("Buffer size {}bits {}bytes {}mb", sizeInBits, (sizeInBits / 8), (sizeInBits / 8) * 1e+6);

        var octreeMemBuffer = MemoryUtil.memAllocInt(voxelRepository.voxels.length);
        int[] voxels = voxelRepository.voxels;
        for (int i = 0, voxelsLength = voxels.length; i < voxelsLength; i++) {
            int voxelData = voxels[i];
            octreeMemBuffer.put(i, voxelData);
        }
        coreSSBORepository.createOctreeBuffer(octreeMemBuffer);
        MemoryUtil.memFree(octreeMemBuffer);
    }
}
