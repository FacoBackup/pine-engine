package com.pine.service.svo;

import java.io.Serializable;

public record VoxelData(float r, float g, float b) implements Serializable {
    public static final int INFO_PER_VOXEL = 3;
}
