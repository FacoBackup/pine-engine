package com.pine.service.svo;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.Serializable;

public class SparseVoxelOctree implements Serializable {
    private final int scale;
    private final int maxDepth;
    private final OctreeNode root = new OctreeNode();
    private final float voxelizationStepSize;
    private final float voxelSize;
    private final int offset;
    private int nodeQuantity = 1;
    private int bufferIndex = 0;
    private int[] voxels;

    public SparseVoxelOctree(int scale, int maxDepth, float voxelizationStepSize) {
        this.scale = scale;
        this.maxDepth = maxDepth;
        this.voxelizationStepSize = Math.min(voxelizationStepSize, .01f);
        this.voxelSize = (float) (scale / Math.pow(2, maxDepth));
        offset = scale / 2;
    }

    public int getNodeQuantity() {
        return nodeQuantity;
    }

    public void insert(Vector3f point, VoxelData data) {
        insertInternal(root, point, data, new Vector3i(0), 0);
    }

    private void insertInternal(OctreeNode node, Vector3f point, VoxelData data, Vector3i position, int depth) {
        node.setData(data);
        if (depth == maxDepth) {
            node.setLeaf(true);
            return;
        }

        float size = (float) (scale / Math.pow(2, depth));
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

    public float getDepth() {
        return maxDepth;
    }

    public int getScale() {
        return scale;
    }

    public float getVoxelizationStepSize() {
        return voxelizationStepSize;
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
//            voxelRepository.voxels[node.getDataIndex()] = node.getData().compress(); // LEAF WILL ONLY STORE COLOR INFORMATION
        }
        voxels[node.getDataIndex()] = node.packVoxelData(bufferIndex);
        for (var child : node.getChildren()) {
            if (child != null && !child.isLeaf()) {
                putData(child);
            }
        }
        for (var child : node.getChildren()) {
            if (child != null) {
                fillStorage(child);
            }
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

    public float getOffset() {
        return offset;
    }
}
