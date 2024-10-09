package com.pine.service.svo;

import java.io.Serializable;

public class OctreeNode implements Serializable {
    public static final int INFO_PER_VOXEL = 2;
    private final OctreeNode[] children = new OctreeNode[8];
    private VoxelData data;
    private boolean isLeaf = false;

    /**
     * Target location of this node's data inside the SSBO buffer
     */
    private int dataIndex;
    /**
     * Indicates whether the child on the index of the bit is a leaf node or not (1 bit for leaf 0 otherwise)
     */
    private int leafMask = 0;

    /**
     * Indicates whether a child is present at the index represented by the bit
     */
    private int childMask = 0;

    public void setData(VoxelData data) {
        this.data = data;
    }

    public VoxelData getData() {
        return data;
    }

    public OctreeNode[] getChildren() {
        return children;
    }

    public void addChild(OctreeNode child, int index) {
        children[index] = child;
    }

    private void prepareData() {
        childMask = 0;
        leafMask = 0;
        for (int i = 0; i < 8; i++) {
            var child = children[i];
            if (child != null) {
                childMask |= (1 << i);
                if (child.isLeaf) {
                    leafMask |= (1 << i);
                }
            }
        }
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int packVoxelData(int childrenGroupIndex) {
        prepareData();
        return (childrenGroupIndex << 16) | (childMask << 8) | leafMask;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    public int getDataIndex() {
        return dataIndex;
    }
}
