package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.service.streaming.texture.TextureStreamableResource;
import com.pine.theme.Icons;

@PBean
public class TerrainSettingsRepository extends Inspectable implements SerializableRepository {
    @MutableField(label = "Casts shadows")
    public boolean castsShadows = true;

    @MutableField(label = "Height map")
    public TextureStreamableResource heightMapTexture;

    @MutableField(label = "Height Scale")
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