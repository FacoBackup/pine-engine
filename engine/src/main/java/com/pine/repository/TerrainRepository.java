package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.inspection.ExecutableField;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.ImporterService;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.impl.TextureService;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.terrain.TerrainService;
import com.pine.theme.Icons;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PBean
public class TerrainRepository extends Inspectable implements SerializableRepository {

    public String bakeId;

    @PInject
    public transient TerrainService terrainService;
    public final String id = UUID.randomUUID().toString();

    @ExecutableField(icon = Icons.terrain, label = "Import terrain")
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
    @InspectableField(label = "Link height map")
    public String heightMapTexture;

    @InspectableField(label = "Height Scale", min = 1)
    public float heightScale = 1;

    @InspectableField(label = "Casts shadows")
    public boolean castsShadows = true;

    public final Map<String, FoliageInstance> foliage = new HashMap<>();

    @PInject
    public transient StreamingService streamingService;

    @PInject
    public transient ImporterService importerService;

    @Override
    public void onSave() {
        terrainService.onSave();
    }

    @Override
    public String getTitle() {
        return "Terrain Settings";
    }

    @Override
    public String getIcon() {
        return Icons.terrain;
    }
}