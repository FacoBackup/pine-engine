package com.pine.service.svo;

import org.joml.Vector3f;

public class LorenzAttractorDemo {
    private static final double SIGMA = 10.0;
    private static final double RHO = 28.0;
    private static final double BETA = 8.0 / 3.0;

    // Time step for numerical integration
    private static final double DT = 0.0001;


    public static void fill(SparseVoxelOctree octree) {
        double x = 1.0, y = 4, z = 12.0;
        int numPoints = 1_000_000;
        for (int i = 0; i < numPoints; i++) {
            double dx = SIGMA * (y - x);
            double dy = x * (RHO - z) - y;
            double dz = x * y - BETA * z;

            x += dx * DT;
            y += dy * DT;
            z += dz * DT;
            octree.insert(new Vector3f((float) x + octree.getOffset(), (float) z, (float) y + octree.getOffset()), new VoxelData(1, 1, 1));
        }
    }
}
