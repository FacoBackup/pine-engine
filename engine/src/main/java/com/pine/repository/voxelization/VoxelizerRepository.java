package com.pine.repository.voxelization;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.theme.Icons;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

@PBean
public class VoxelizerRepository extends Inspectable implements SerializableRepository {
    public static final byte OCCUPIED_VOXEL = 1;
    public final List<Octree> octreeBuffer = new ArrayList<>();
    public transient IntBuffer octreeMemBuffer = null;
    public transient FloatBuffer voxelDataMemBuffer = null;
    public float[] voxelDataBuffer = null;
    public transient byte[][][] voxelGrid = null;
    public transient ShaderStorageBufferObject octreeSSBO;
    public transient ShaderStorageBufferObject voxelDataSSBO;

    @MutableField(label = "Voxel grid resolution", max = 512, min = 32)
    public int gridResolution = 128;

    @MutableField(label = "Voxel grid max subdivisions", max = 64, min = 4)
    public int maxDepth = 32;

    public float voxelSize = 1;

    @Override
    public String getTitle() {
        return "Scene voxelization";
    }

    @Override
    public String getIcon() {
        return Icons.apps;
    }
}
