package com.pine.service.svo;

import java.io.Serializable;

public class OctreeNode implements Serializable {
    public static final int INFO_PER_VOXEL = 2;
    private boolean isLeaf = false;
    private final OctreeNode[] children = new OctreeNode[8];
    private VoxelData data;

    public void setData(VoxelData data) {
        this.data = data;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public VoxelData getData() {
        return data;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public OctreeNode[] getChildren() {
        return children;
    }

    public void addChild(OctreeNode child, int index) {
        children[index] = child;
    }

    public int getChildMask() {
        int childMask = 0;
        for (int i = 0; i < 8; i++) {
            var child = children[i];
            if (child != null) {
                childMask |= (1 << i);
            }
        }
        return childMask;
    }
}
