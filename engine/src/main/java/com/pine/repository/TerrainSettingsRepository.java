package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.ExecutableField;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.theme.Icons;

import javax.swing.*;

@PBean
public class TerrainSettingsRepository extends Inspectable implements SerializableRepository {

    @ExecutableField(icon = Icons.terrain, label = "Process terrain")
    public void process(){
    }

    @InspectableField(label = "Casts shadows")
    public boolean castsShadows = true;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Height map")
    public String heightMapTexture;

    @InspectableField(label = "Height Scale")
    public float heightScale = 1;

    @Override
    public String getTitle() {
        return "Terrain Settings";
    }

    @Override
    public String getIcon() {
        return Icons.terrain;
    }
}