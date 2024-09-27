package com.pine.repository;

import com.pine.PBean;
import com.pine.component.ResourceRef;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.inspection.ResourceField;
import com.pine.service.resource.primitives.texture.TextureResource;
import com.pine.service.resource.resource.ResourceType;
import com.pine.theme.Icons;

@PBean
public class TerrainSettingsRepository extends Inspectable {
    @MutableField(label = "Casts shadows")
    public boolean castsShadows = true;

    @ResourceField(type = ResourceType.TEXTURE)
    @MutableField(label = "Height map")
    public ResourceRef<TextureResource> heightMapTexture;

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