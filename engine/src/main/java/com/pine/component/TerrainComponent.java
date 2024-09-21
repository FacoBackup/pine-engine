package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.repository.rendering.RuntimeDrawDTO;
import com.pine.service.resource.primitives.texture.TextureResource;

import java.util.Set;

@PBean
public class TerrainComponent extends AbstractComponent<TerrainComponent> {
    @MutableField(label = "Casts shadows")
    public boolean castsShadows = true;
    @MutableField(label = "Casts shadows")
    public TextureResource heightMapTexture;
    @MutableField(label = "Height Scale")
    public float heightScale = 1;
    public transient RuntimeDrawDTO request;

    public TerrainComponent(Integer entityId) {
        super(entityId);
    }

    public TerrainComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class, CullingComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Terrain";
    }
}
