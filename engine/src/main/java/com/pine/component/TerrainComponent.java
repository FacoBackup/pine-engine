package com.pine.component;

import com.pine.injection.EngineInjectable;
import com.pine.inspection.MutableField;
import com.pine.service.resource.primitives.texture.Texture;

import java.util.Set;

@EngineInjectable
public class TerrainComponent extends AbstractComponent<TerrainComponent> {
    @MutableField(label = "Casts shadows")
    public boolean castsShadows = true;
    @MutableField(label = "Casts shadows")
    public Texture heightMapTexture;
    @MutableField(label = "Height Scale")
    public float heightScale = 1;

    public TerrainComponent(Integer entityId) {
        super(entityId);
    }

    public TerrainComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Terrain";
    }
}
