package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.inspection.ExecutableField;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.service.voxelization.svo.SVOGrid;
import com.pine.service.voxelization.VoxelizationService;
import com.pine.theme.Icons;

@PBean
public class VoxelRepository extends Inspectable implements SerializableRepository {
    public SVOGrid grid;

    @PInject
    public transient VoxelizationService voxelizationService;
    @ExecutableField(icon = Icons.apps, label = "Bake voxelized scene")
    public void process(){
        if(!voxelizationService.bake()){
            getLogger().error("Already voxelizing scene");
        }
    }
    @InspectableField(label = "Chunk grid size", min = 1)
    public int chunkGridSize = 4;

    @InspectableField(label = "Chunk size", min = 1)
    public int chunkSize = 100;

    @InspectableField(label = "Max depth", min = 1, max = 10)
    public int maxDepth = 6;

    @InspectableField(group = "Debug", label = "Render voxels")
    public boolean showVoxels = false;

    @InspectableField(group = "Debug", label = "Random colors")
    public boolean randomColors = true;

    @InspectableField(group = "Debug", label = "Show ray search count")
    public boolean showRaySearchCount;

    @InspectableField(group = "Debug", label = "Show ray test count")
    public boolean showRayTestCount;


    @Override
    public String getTitle() {
        return "Scene voxelizer";
    }

    @Override
    public String getIcon() {
        return Icons.apps;
    }
}
