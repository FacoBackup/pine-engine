package com.pine.service.svo;

import java.io.Serializable;

public record VoxelData(int r, int g, int b) implements Serializable {
    public int compress() {
        return (r << 16) | (g << 9) | (b << 2);
    }
}
