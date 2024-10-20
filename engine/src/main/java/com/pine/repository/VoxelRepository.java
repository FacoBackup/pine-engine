package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.service.svo.SVOGrid;
import com.pine.theme.Icons;

@PBean
public class VoxelRepository extends Inspectable implements SerializableRepository {
    public SVOGrid grid;

    @MutableField(label = "Chunk grid size", min = 1)
    public int chunkGridSize;

    @MutableField(label = "Chunk size", min = 1)
    public int chunkSize;

    @MutableField(label = "Scene scale", min = 1)
    public int gridResolution = 20;

    @MutableField(label = "Max depth", min = 1, max = 10)
    public int maxDepth = 4;

    @MutableField(label = "Voxelization step size", min = 0, max = 1)
    public float voxelizationStepSize = .1f;

    @MutableField(group = "Debug", label = "Random colors")
    public boolean randomColors;

    @MutableField(group = "Debug", label = "Show ray search count")
    public boolean showRaySearchCount;

    @MutableField(group = "Debug", label = "Show ray test count")
    public boolean showRayTestCount;

    @Override
    public String getTitle() {
        return "Scene voxelizer";
    }

    @Override
    public String getIcon() {
        return Icons.apps;
    }

    public int getVoxelCount() {
        // TODO
//        if (voxels != null) {
//            return voxels.length;
//        }
        return 0;
    }
}
