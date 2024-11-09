package com.pine.service.voxelization.svo;

public record DensityVoxelData(float density) implements VoxelData{

    @Override
    public int compress() {
        return (int) (density * 65535.0f);
    }
}
