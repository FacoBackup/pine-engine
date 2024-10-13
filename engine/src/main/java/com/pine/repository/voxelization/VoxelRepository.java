package com.pine.repository.voxelization;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.service.svo.OctreeNode;
import com.pine.service.svo.SparseVoxelOctree;
import com.pine.theme.Icons;
import org.joml.Vector3f;

import java.nio.IntBuffer;

@PBean
public class VoxelRepository extends Inspectable implements SerializableRepository {
    public transient int[] voxels = null;

    @MutableField(label = "Grid size", min = 0)
    public int gridResolution = 20;

    @MutableField(label = "Center")
    public Vector3f center = new Vector3f(0);

    @MutableField(label = "Max depth", min = 1, max = 10)
    public int maxDepth = 4;

    @Override
    public String getTitle() {
        return "Voxelized scene";
    }

    @Override
    public String getIcon() {
        return Icons.apps;
    }

    public int getVoxelCount() {
        if (voxels != null) {
            return voxels.length / OctreeNode.INFO_PER_VOXEL;
        }
        return 0;
    }
}
