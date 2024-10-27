package com.pine.component;

import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.StreamableResourceType;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MeshComponent extends AbstractComponent {
    @InspectableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @InspectableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

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

    // INSTANCING
    @InspectableField(group = "Instancing", label = "Enable instancing")
    public boolean isInstancedRendering = false;
    @InspectableField(group = "Instancing", label = "Number of instances", min = 1)
    public int numberOfInstances = 10;
    @InspectableField(group = "Instancing", label = "Scene members")
    public List<TransformationComponent> instances = new ArrayList<>();

    public transient RenderingRequest renderRequest;

    // CULLING
    @InspectableField(group = "Culling", label = "Enable culling")
    public boolean isCullingEnabled = true;
    @InspectableField(group = "Culling", label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 200;
    @InspectableField(group = "Culling", label = "Bounding box size")
    public final Vector3f boundingBoxSize = new Vector3f(1);


    public float distanceFromCamera = 0f;

    public MeshComponent(Entity entity) {
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


    @Override
    public AbstractComponent cloneComponent(Entity entity) {
        var clone = (MeshComponent) super.cloneComponent(entity);
        clone.instances = new ArrayList<>();
        if(clone.isInstancedRendering){
            this.instances.forEach(i -> {
                clone.instances.add((TransformationComponent) i.cloneComponent(entity));
            });
        }
        return clone;
    }
}
