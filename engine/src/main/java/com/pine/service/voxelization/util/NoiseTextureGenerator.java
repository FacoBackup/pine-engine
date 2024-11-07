package com.pine.service.voxelization.util;

import com.pine.service.voxelization.LorenzAttractorDemo;
import com.pine.service.voxelization.svo.DensityVoxelData;
import com.pine.service.voxelization.svo.SparseVoxelOctree;
import org.joml.Vector3f;

import java.util.Random;

public class NoiseTextureGenerator {
    private static final int SEED = 42;

    public static void generatePerlinWorleyNoise(int width, int height, int depth, float threshold, int perlinScale, int worleyScale, SparseVoxelOctree octree) {
        Random random = new Random(SEED);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    float perlinValue = generatePerlinNoise(x, y, z, perlinScale);
                    float worleyValue = generateWorleyNoise(x, y, z, worleyScale, random);

                    float density = 0.5f * perlinValue + 0.5f * worleyValue;

                    if (density < threshold) {
                        octree.insert(new Vector3f(x, y, z), new DensityVoxelData(0.5f * perlinValue + 0.5f * worleyValue));
                    }
                }
            }
        }
    }

    private static float generatePerlinNoise(int x, int y, int z, int scale) {
        return (float) Math.sin((x + y + z) / (float) scale);
    }

    private static float generateWorleyNoise(int x, int y, int z, int scale, Random random) {
        float nearestDistance = Float.MAX_VALUE;
        int numCells = scale * scale * scale;

        for (int i = 0; i < numCells; i++) {
            float cx = random.nextFloat() * scale;
            float cy = random.nextFloat() * scale;
            float cz = random.nextFloat() * scale;

            float distance = distance(x, y, z, cx, cy, cz);
            nearestDistance = Math.min(nearestDistance, distance);
        }

        return nearestDistance / (float) scale;
    }

    private static float distance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1));
    }
}
