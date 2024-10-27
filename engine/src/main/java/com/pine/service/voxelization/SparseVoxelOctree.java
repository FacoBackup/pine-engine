package com.pine.service.voxelization;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.Serializable;
import java.util.UUID;

public class SparseVoxelOctree implements Serializable {
    private final int size;
    private final int maxDepth;
    private transient OctreeNode root = new OctreeNode();
    private final float voxelSize;
    private final BoundingBox boundingBox;
    private final Vector3f center;
    private int nodeQuantity = 1;
    private transient int bufferIndex = 0;
    private transient int[] voxels;
    private final String id = UUID.randomUUID().toString();

    public SparseVoxelOctree(Vector3f center, int size, int maxDepth) {
        this.center = center;
        this.size = size;
        this.maxDepth = maxDepth;
        this.voxelSize = (float) (size / Math.pow(2, maxDepth));
        this.boundingBox = new BoundingBox();
        boundingBox.max = new Vector3f(center).add(size / 2f, size / 2f, size / 2f);
        boundingBox.min = new Vector3f(center).sub(size / 2f, size / 2f, size / 2f);
    }

    public int getNodeQuantity() {
        return nodeQuantity;
    }

    public void insert(Vector3f point, VoxelData data) {
        if (boundingBox.intersects(point)) {
            worldToChunkLocal(point);
            insertInternal(root, point, data, new Vector3i(0), 0);
        }
    }

    private void insertInternal(OctreeNode node, Vector3f point, VoxelData data, Vector3i position, int depth) {
        node.setData(data);
        if (depth == maxDepth) {
            node.setLeaf(true);
            return;
        }

        float size = (float) (this.size / Math.pow(2, depth));
        Vector3i childPos = new Vector3i(
                point.x >= ((size * position.x) + (size / 2)) ? 1 : 0,
                point.y >= ((size * position.y) + (size / 2)) ? 1 : 0,
                point.z >= ((size * position.z) + (size / 2)) ? 1 : 0
        );

        int childIndex = (childPos.x << 0) | (childPos.y << 1) | (childPos.z << 2);

        position.x = (position.x << 1) | childPos.x;
        position.y = (position.y << 1) | childPos.y;
        position.z = (position.z << 1) | childPos.z;

        if (node.getChildren()[childIndex] != null) {
            insertInternal(node.getChildren()[childIndex], point, data, position, depth + 1);
        } else {
            var child = new OctreeNode();
            node.addChild(child, childIndex);
            insertInternal(child, point, data, position, depth + 1);

            // Leaf nodes don't need to be included on the tree
            if (!child.isLeaf()) {
                nodeQuantity++;
            }
        }
    }

    public OctreeNode getRoot() {
        return root;
    }

    public int getDepth() {
        return maxDepth;
    }

    public int getSize() {
        return size;
    }

    public float getVoxelSize() {
        return voxelSize;
    }

    public int[] buildBuffer() {
        bufferIndex = 0;
        voxels = new int[nodeQuantity];
        putData(root);
        fillStorage(root);
        return voxels;
    }

    private void fillStorage(OctreeNode node) {
        if (node.isLeaf()) {
            return;
        }

        voxels[node.getDataIndex()] = node.packVoxelData(bufferIndex);
        boolean isParentOfLeaf = true;
        for (var child : node.getChildren()) {
            if (child != null && !child.isLeaf()) {
                putData(child);
                isParentOfLeaf = false;
            }
        }

        for (var child : node.getChildren()) {
            if (child != null) {
                fillStorage(child);
            }
        }
        if (isParentOfLeaf && node.getData() != null) {
            voxels[node.getDataIndex()] = node.packVoxelData(node.getData().compress());
        }
    }

    /**
     * Non-leaf nodes store 16 bit pointer to child group + 8 bit child mask + 8 bit leaf mask
     * @param node
     */
    private void putData(OctreeNode node) {
        node.setDataIndex(bufferIndex);
        bufferIndex++;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void purgeData() {
        voxels = null;
        root = null;
    }

    public String getId() {
        return id;
    }

    private void worldToChunkLocal(Vector3f worldCoordinate) {
        float minX = center.x - size / 2f;
        float minY = center.y - size / 2f;
        float minZ = center.z - size / 2f;

        worldCoordinate.x = worldCoordinate.x - minX;
        worldCoordinate.y = worldCoordinate.y - minY;
        worldCoordinate.z = worldCoordinate.z - minZ;
    }

}
