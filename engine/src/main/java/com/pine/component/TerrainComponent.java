package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.inspection.ResourceField;
import com.pine.inspection.ResourceRef;
import com.pine.repository.rendering.PrimitiveRenderingRequest;
import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;
import com.pine.service.resource.primitives.texture.TextureResource;
import com.pine.service.resource.resource.ResourceType;

import java.util.Set;

@PBean
public class TerrainComponent extends AbstractComponent<TerrainComponent> {
    @MutableField(label = "Casts shadows")
    public boolean castsShadows = true;

    @ResourceField(type = ResourceType.TEXTURE)
    @MutableField(label = "Height map")
    public ResourceRef<TextureResource> heightMapTexture;

    public transient MeshPrimitiveResource meshInstance;

    @MutableField(label = "Height Scale")
    public float heightScale = 1;

    public transient PrimitiveRenderingRequest request;

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
