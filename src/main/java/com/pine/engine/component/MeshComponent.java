package com.pine.engine.component;

import com.pine.common.inspection.InspectableField;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.rendering.RenderingRequest;
import com.pine.engine.repository.streaming.StreamableResourceType;

import java.util.Set;


public class MeshComponent extends AbstractComponent {
    @InspectableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(label = "Mesh LOD 0")
    public String lod0;

    @InspectableField(label = "Use LOD 0 when distance")
    public float lod0DistanceUntil = 50;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(label = "Mesh LOD 1")
    public String lod1;

    @InspectableField(label = "Use LOD 1 when distance")
    public float lod1DistanceUntil = 100;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(label = "Mesh LOD 2")
    public String lod2;

    @InspectableField(label = "Use LOD 2 when distance")
    public float lod2DistanceUntil = 150;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(label = "Mesh LOD 3")
    public String lod3;

    @InspectableField(label = "Use LOD 3 when distance")
    public float lod3DistanceUntil = 200;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(label = "Mesh LOD 4")
    public String lod4;

    public RenderingRequest renderRequest;

    public MeshComponent(String entity) {
        super(entity);
    }

    @Override
    public Set<ComponentType> getDependencies() {
        return Set.of(ComponentType.TRANSFORMATION, ComponentType.CULLING);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.MESH;
    }

    @Override
    public AbstractComponent cloneComponent(Entity entity) {
        var clone = (MeshComponent) super.cloneComponent(entity);
        clone.renderRequest = null;
        return clone;
    }
}
