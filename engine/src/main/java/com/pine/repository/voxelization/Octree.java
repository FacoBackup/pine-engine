package com.pine.repository.voxelization;

import java.io.Serializable;

public class Octree implements Serializable {
    public int childMask;         // 8-bit bitmask for child occupancy
    public int firstChildIndex;   // Index of the first child in the flat array
    public int voxelDataIndex;
    public int octant;
}
