package com.pine.engine.repository;

import com.pine.common.Icons;
import com.pine.common.SerializableRepository;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.inspection.ExecutableField;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.service.voxelization.VoxelizationService;

@PBean
public class VoxelRepository extends Inspectable implements SerializableRepository {
    @PInject
    public transient VoxelizationService voxelizationService;
    @ExecutableField(icon = Icons.apps, label = "Bake voxelized scene")
    public void process(){
        if(!voxelizationService.bake()){
            getLogger().error("Already voxelizing scene");
        }
    }
    @InspectableField(label = "Chunk world size", min = 1)
    public int chunkGridSize = 4;

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
