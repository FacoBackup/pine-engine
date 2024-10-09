package com.pine.service.svo;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.Serializable;
import java.util.Vector;

public class SparseVoxelOctree implements Serializable {
    private final int resolution;
    private final int maxDepth;
    private final OctreeNode root = new OctreeNode();
    private int nodeQuantity = 1;

    public SparseVoxelOctree(int resolution, int maxDepth) {
        this.resolution = resolution;
        this.maxDepth = maxDepth;
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

        float size = (float) (resolution / Math.pow(2, depth));
        Vector3i childPos = new Vector3i(
                ((point.x >= (size * position.x) + size / 2) ? 1 : 0),
                ((point.y >= (size * position.y) + size / 2) ? 1 : 0),
                ((point.z >= (size * position.z) + size / 2) ? 1 : 0)
        );

        int childIndex = (childPos.x << 0) | (childPos.y << 1) | (childPos.z << 2);

        position.x = (position.x << 1) | childPos.x;
        position.y = (position.y << 1) | childPos.y;
        position.z = (position.z << 1) | childPos.z;

        if (node.getChildren()[childIndex] != null) {
            insertInternal(node.getChildren()[childIndex], point, data, position, depth + 1);
        } else {
            var child = new OctreeNode();
            nodeQuantity++;
            node.addChild(child, childIndex);
            insertInternal(child, point, data, position, depth + 1);
        }
    }

    public OctreeNode getRoot() {
        return root;
    }
}
