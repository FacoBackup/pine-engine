package com.pine.service.svo;

import java.io.Serializable;

public record VoxelData(float r, float g, float b) implements Serializable {
    public int compress() {
        // Normalize the vector to [0, 1] range
        float x = Math.max(0, Math.min(1, (r + 1) * 0.5f));
        float y = Math.max(0, Math.min(1, (g + 1) * 0.5f));
        float z = Math.max(0, Math.min(1, (b + 1) * 0.5f));

        // Quantize each component to 10 bits
        int xInt = (int)(x * 1023); // 10 bits
        int yInt = (int)(y * 1023); // 10 bits
        int zInt = (int)(z * 1023); // 10 bits

        // Pack into a 32-bit integer
        return (xInt << 20) | (yInt << 10) | zInt;
    }
}
