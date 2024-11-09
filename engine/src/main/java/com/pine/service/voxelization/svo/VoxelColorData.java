package com.pine.service.voxelization.svo;

public record VoxelColorData(int r, int g, int b) implements VoxelData {

    @Override
    public int compress() {
        int red = (r / 2) & 0x7F;      // Scale down and mask to 7 bits
        int green = g & 0xFF;          // No change, 8 bits
        int blue = (b / 2) & 0x7F;     // Scale down and mask to 7 bits

        return (red << 16) | (green << 8) | blue;
    }
}
