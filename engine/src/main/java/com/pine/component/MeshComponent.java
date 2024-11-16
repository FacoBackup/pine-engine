package com.pine.component;

import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.StreamableResourceType;

import java.util.Map;
import java.util.Set;


public class MeshComponent extends AbstractComponent {
    @InspectableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(group = "Mesh", label = "Mesh LOD 0")
    public String lod0;

    @InspectableField(group = "Mesh", label = "Use LOD 0 when distance")
    public float lod0DistanceUntil = 50;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(group = "Mesh", label = "Mesh LOD 1")
    public String lod1;

    @InspectableField(group = "Mesh", label = "Use LOD 1 when distance")
    public float lod1DistanceUntil = 100;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(group = "Mesh", label = "Mesh LOD 2")
    public String lod2;

    @InspectableField(group = "Mesh", label = "Use LOD 2 when distance")
    public float lod2DistanceUntil = 150;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(group = "Mesh", label = "Mesh LOD 3")
    public String lod3;

    @InspectableField(group = "Mesh", label = "Use LOD 3 when distance")
    public float lod3DistanceUntil = 200;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(group = "Mesh", label = "Mesh LOD 4")
    public String lod4;

    public RenderingRequest renderRequest;

    // CULLING
    @InspectableField(group = "Culling", label = "Enable culling")
    public boolean isCullingEnabled = true;
    @InspectableField(group = "Culling", label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 200;
    @InspectableField(group = "Culling", label = "Sphere radius")
    public final float cullingSphereRadius = 1;


    public float distanceFromCamera = 0f;

    public MeshComponent(String entity) {
        super(entity);
    }

    @Override
    public Set<ComponentType> getDependencies() {
        return Set.of(ComponentType.TRANSFORMATION);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.MESH;
    }

    public boolean canRender(boolean isDisableCulling, Map<String, Boolean> hiddenEntities) {
        return !hiddenEntities.containsKey(getEntityId()) && renderRequest != null && renderRequest.modelMatrix != null && renderRequest.mesh != null && (!renderRequest.isCulled || isDisableCulling);
    }

    @Override
    public AbstractComponent cloneComponent(Entity entity) {
        var clone = (MeshComponent) super.cloneComponent(entity);
        clone.renderRequest = null;
        return clone;
    }
}
