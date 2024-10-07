package com.pine.service;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.voxelization.Octree;
import com.pine.repository.voxelization.VoxelizerRepository;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.service.streaming.mesh.MeshService;
import com.pine.service.streaming.mesh.MeshStreamData;
import com.pine.tasks.SyncTask;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import static com.pine.repository.voxelization.VoxelizerRepository.OCCUPIED_VOXEL;

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

    private Vector3f currentMeshMinBounds;
    private Vector3f currentMeshMaxBounds;
    private int bounding;
    private boolean needsPackaging = true;

    @Override
    public void sync() {
        if (voxelizerRepository.octreeBuffer.isEmpty() || !needsPackaging) {
            return;
        }
        packageData();
        needsPackaging = false;
    }

    public void buildFromScratch() {
        voxelizerRepository.voxelGrid = new byte[voxelizerRepository.gridResolution + 1][voxelizerRepository.gridResolution + 1][voxelizerRepository.gridResolution + 1];
        voxelizerRepository.voxelDataBuffer.clear();
        bounding = voxelizerRepository.gridResolution / 2;

        RenderingRequest request = renderingRepository.requests.getFirst();
        MeshStreamData rawMeshData = meshService.stream(request.mesh);
        traverseMesh(rawMeshData, request.transformation.globalMatrix);


        float sceneWidth = voxelizerRepository.sceneMax.x - voxelizerRepository.sceneMin.x;
        float sceneHeight = voxelizerRepository.sceneMax.y - voxelizerRepository.sceneMin.y;
        float sceneDepth = voxelizerRepository.sceneMax.z - voxelizerRepository.sceneMin.z;
        float sceneSize = Math.max(sceneWidth, Math.max(sceneHeight, sceneDepth));

        voxelizerRepository.voxelSize = sceneSize / voxelizerRepository.gridResolution;

        final int calculatedDepth = (int) Math.floor(Math.log(voxelizerRepository.gridResolution / voxelizerRepository.voxelSize) / Math.log(2));
        voxelizerRepository.computedMaxDepth = Math.min(calculatedDepth, 10);


        buildOctree(new Vector3f(voxelizerRepository.sceneMin), new Vector3f(voxelizerRepository.sceneMax), 0);
        needsPackaging = true;
    }

    private void packageData() {
        cleanStorage();
        fillStorage();
        createStorage();
    }

    private void fillStorage() {
        int offset = 0;
        for (var octree : voxelizerRepository.octreeBuffer) {
            voxelizerRepository.octreeMemBuffer.put(offset, octree.childMask);
            offset++;
            voxelizerRepository.octreeMemBuffer.put(offset, octree.firstChildIndex);
            offset++;
            voxelizerRepository.octreeMemBuffer.put(offset, octree.voxelDataIndex);
            offset++;
            voxelizerRepository.octreeMemBuffer.put(offset, octree.octant);
            offset++;
        }

        for (int i = 0; i < voxelizerRepository.voxelDataBuffer.size(); i++) {
            voxelizerRepository.voxelDataMemBuffer.put(i, voxelizerRepository.voxelDataBuffer.get(i));
        }
    }

    private void cleanStorage() {
        if (voxelizerRepository.octreeSSBO != null) {
            resourceService.remove(voxelizerRepository.octreeSSBO.getId());
            resourceService.remove(voxelizerRepository.voxelDataSSBO.getId());
        }
        voxelizerRepository.octreeMemBuffer = MemoryUtil.memAllocInt(voxelizerRepository.octreeBuffer.size() * INFO_PER_VOXEL);
        voxelizerRepository.voxelDataMemBuffer = MemoryUtil.memAllocFloat((int) (INFO_PER_VOXEL_DATA * Math.pow(voxelizerRepository.gridResolution, 3)));
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

    private boolean pointInTriangle(Vector4f A, Vector4f B, Vector4f C, float x, float y, float z) {
        // Compute the voxel center and half-size
        Vector3f voxelCenter = new Vector3f(
                currentMeshMinBounds.x + x * voxelizerRepository.voxelSize + voxelizerRepository.voxelSize / 2,
                currentMeshMinBounds.y + y * voxelizerRepository.voxelSize + voxelizerRepository.voxelSize / 2,
                currentMeshMinBounds.z + z * voxelizerRepository.voxelSize + voxelizerRepository.voxelSize / 2
        );

        Vector3f halfSize = new Vector3f(voxelizerRepository.voxelSize / 2, voxelizerRepository.voxelSize / 2, voxelizerRepository.voxelSize / 2);
        // Implement a robust triangle-box intersection test (e.g., SAT)
        return true;//satTriangleBoxIntersect(A, B, C, voxelCenter, halfSize);
    }


    private void traverseMesh(MeshStreamData rawMeshData, Matrix4f globalMatrix) {
        int[] indices = rawMeshData.indices();
        float[] vertices = rawMeshData.vertices();
        currentMeshMinBounds = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        currentMeshMaxBounds = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);


        for (int i = 0; i < indices.length; i++) {
            Vector4f vertex = new Vector4f(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1], vertices[indices[i] * 3 + 2], 1).mul(globalMatrix);
            Vector3f v = new Vector3f(vertex.x, vertex.y, vertex.z);
            currentMeshMinBounds.min(v);
            currentMeshMaxBounds.max(v);
        }

        for (int i = 0; i < indices.length; i += 3) {
            Vector4f v0 = new Vector4f(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1], vertices[indices[i] * 3 + 2], 1).mul(globalMatrix);
            Vector4f v1 = new Vector4f(vertices[indices[i + 1] * 3], vertices[indices[i + 1] * 3 + 1], vertices[indices[i + 1] * 3 + 2], 1).mul(globalMatrix);
            Vector4f v2 = new Vector4f(vertices[indices[i + 2] * 3], vertices[indices[i + 2] * 3 + 1], vertices[indices[i + 2] * 3 + 2], 1).mul(globalMatrix);

            // Compute the bounding box of the triangle
            Vector4f triMin = v0.min(v1).min(v2);
            Vector4f triMax = v0.max(v1).max(v2);

            // Determine the range of voxels intersected by the triangle

            int minX = (int) ((triMin.x - voxelizerRepository.sceneMin.x) / voxelizerRepository.voxelSize);  // Find voxel index
            int maxX = (int) ((triMax.x - voxelizerRepository.sceneMin.x) / voxelizerRepository.voxelSize);

            int minY = (int) ((triMin.y - voxelizerRepository.sceneMin.y) / voxelizerRepository.voxelSize);
            int maxY = (int) ((triMax.y - voxelizerRepository.sceneMin.y) / voxelizerRepository.voxelSize);

            int minZ = (int) ((triMin.z - voxelizerRepository.sceneMin.z) / voxelizerRepository.voxelSize);
            int maxZ = (int) ((triMax.z - voxelizerRepository.sceneMin.z) / voxelizerRepository.voxelSize);


            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        if (pointInTriangle(v0, v1, v2, x, y, z)) {
                            voxelizerRepository.voxelGrid[x][y][z] = OCCUPIED_VOXEL;
                        }
                    }
                }
            }
        }
    }

    private Octree buildOctree(Vector3f min, Vector3f max, int depth) {
        Octree node = new Octree();
        if (depth == 0) {
            voxelizerRepository.octreeBuffer.clear();
            voxelizerRepository.octreeBuffer.add(node);
        }

        node.voxelDataIndex = storeVoxelData(min, max, depth);
        boolean isEmpty = regionIsEmpty(min, max);
        // If depth is too large or the region is empty, mark this as a leaf node
        if (depth == voxelizerRepository.computedMaxDepth || isEmpty) {
            return node;
        }
        node.firstChildIndex = voxelizerRepository.octreeBuffer.size();

        for (int i = 0; i < 8; i++) {
            voxelizerRepository.octreeBuffer.add(new Octree());
        }

        for (int i = 0; i < 8; i++) {
            Octree octree = voxelizerRepository.octreeBuffer.get(i);
            node.childMask |= (1 << i);
            Vector3f childMin = computeChildMin(i, min, max);
            Vector3f childMax = computeChildMax(i, min, max);

            Octree childNode = buildOctree(childMin, childMax, depth + 1);
            octree.childMask = childNode.childMask;
            octree.voxelDataIndex = childNode.voxelDataIndex;
            octree.firstChildIndex = childNode.firstChildIndex;
            octree.octant = i;
        }

        return node;
    }

    boolean regionIsEmpty(Vector3f min, Vector3f max) {
        for (int x = (int) min.x; x < max.x; ++x) {
            for (int y = (int) min.y; y < max.y; ++y) {
                for (int z = (int) min.z; z < max.z; ++z) {
                    if (voxelizerRepository.voxelGrid[x + bounding][y + bounding][z + bounding] == OCCUPIED_VOXEL) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Vector3f computeChildMin(int childIndex, Vector3f min, Vector3f max) {
        Vector3f size = new Vector3f();
        max.sub(min, size);

        Vector3f halfSize = new Vector3f(size).mul(0.5f);
        float x = (childIndex & 1) == 0 ? min.x : min.x + halfSize.x;
        float y = (childIndex & 2) == 0 ? min.y : min.y + halfSize.y;
        float z = (childIndex & 4) == 0 ? min.z : min.z + halfSize.z;

        return new Vector3f(x, y, z);
    }

    private Vector3f computeChildMax(int childIndex, Vector3f min, Vector3f max) {
        Vector3f size = new Vector3f();
        max.sub(min, size);

        Vector3f halfSize = new Vector3f(size).mul(0.5f);

        float x = (childIndex & 1) == 0 ? min.x + halfSize.x : max.x;
        float y = (childIndex & 2) == 0 ? min.y + halfSize.y : max.y;
        float z = (childIndex & 4) == 0 ? min.z + halfSize.z : max.z;

        return new Vector3f(x, y, z);
    }

    private int storeVoxelData(Vector3f min, Vector3f max, int depth) {
        Vector3f center = new Vector3f(min).add(max).mul(.5f);
        voxelizerRepository.voxelDataBuffer.add(center.x);
        voxelizerRepository.voxelDataBuffer.add(center.y);
        voxelizerRepository.voxelDataBuffer.add(center.z);
        voxelizerRepository.voxelDataBuffer.add((float) (voxelizerRepository.gridResolution/Math.pow(2, depth)));
        return 2;
    }
}
