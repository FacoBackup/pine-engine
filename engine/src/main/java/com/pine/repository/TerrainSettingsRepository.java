package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.theme.Icons;

@PBean
public class TerrainSettingsRepository extends Inspectable implements SerializableRepository {
    @InspectableField(label = "Casts shadows")
    public boolean castsShadows = true;

    @InspectableField(label = "Height map")
    public TextureResourceRef heightMapTexture;

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