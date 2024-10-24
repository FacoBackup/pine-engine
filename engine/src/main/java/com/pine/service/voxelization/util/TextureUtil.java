package com.pine.service.voxelization.util;

import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.voxelization.VoxelData;

public class TextureUtil {
    private static int[] uvToPixelCoords(float u, float v, int imgWidth, int imgHeight) {
        // Clamp UV coordinates to ensure they are within [0,1]
        u = Math.max(0, Math.min(u, 1));
        v = Math.max(0, Math.min(v, 1));

        // Correct horizontal flipping by flipping the U coordinate
        int x = (int) (u * (imgWidth - 1));
        int y = (int) (v * (imgHeight - 1));

        return new int[]{x, y};
    }

    public static VoxelData sampleTextureAtUV(TextureStreamData image, float u, float v) {
        if (image == null) {
            return new VoxelData(0, 0, 0);
        }

        int[] pixelCoords = uvToPixelCoords(u, v, image.width, image.height);
        int x = pixelCoords[0];
        int y = pixelCoords[1];

        int pixelIndex = (y * image.width + x) * 4; // 4 bytes per pixel (RGBA)

        int r = (image.imageBuffer.get(pixelIndex) & 0xFF);
        int g = (image.imageBuffer.get(pixelIndex + 1) & 0xFF);
        int b = (image.imageBuffer.get(pixelIndex + 2) & 0xFF);

        return new VoxelData(r, g, b);
    }
}
