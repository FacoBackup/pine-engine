package com.pine.service.voxelization.svo;

import com.pine.service.voxelization.util.BoundingBox;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SVOGrid implements Serializable {
    public final List<SparseVoxelOctree> chunks = new ArrayList<>();

    public SVOGrid(int chunkSize, int gridSize, int maxDepth) {
        int halfSize = gridSize / 2;
        for (int x = -halfSize; x < halfSize; x++) {
            for (int z = -halfSize; z < halfSize; z++) {
                int centerX = x * chunkSize + chunkSize / 2;
                int centerZ = z * chunkSize + chunkSize / 2;
                chunks.add(new SparseVoxelOctree(new Vector3f(centerX, 0, centerZ), chunkSize, maxDepth));
            }
        }
    }

    public List<SparseVoxelOctree> getIntersectingChunks(BoundingBox bb) {
        List<SparseVoxelOctree> chunksThatIntersect = new ArrayList<>();
        for (var chunk : chunks) {
            if (chunk.getBoundingBox().intersects(bb)) {
                chunksThatIntersect.add(chunk);
            }
        }
        return chunksThatIntersect;
    }
}
