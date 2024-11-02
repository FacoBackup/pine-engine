package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.inspection.ExecutableField;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.terrain.TerrainService;
import com.pine.theme.Icons;

@PBean
public class TerrainRepository extends Inspectable implements SerializableRepository {

    public String bakeId;

    @PInject
    public transient TerrainService terrainService;

    @ExecutableField(icon = Icons.terrain, label = "Process terrain")
    public void process(){
        if(heightMapTexture == null){
            getLogger().error("No height map texture configured");
            return;
        }
        if(!terrainService.bake()){
            getLogger().error("Already processing terrain");
        }
    }

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Height map")
    public String heightMapTexture;

    @InspectableField(label = "Normal Offset", min = 0)
    public float normalOffset = .01f;

    @InspectableField(label = "Height Scale", min = 1)
    public float heightScale = 1;

    @InspectableField(label = "Plane Scale", min = 1)
    public int planeScale = 1;

    @InspectableField(label = "Casts shadows")
    public boolean castsShadows = true;

    @Override
    public String getTitle() {
        return "Terrain Settings";
    }

    @Override
    public String getIcon() {
        return Icons.terrain;
    }
}