package com.pine.component;

import com.pine.inspection.MutableField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class MeshComponent extends AbstractComponent {
    @MutableField(label = "Casts shadow")
    public boolean castsShadows = true;
    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @MutableField(label = "Material", isResource = true)
    public String material;
    @MutableField(group = "Mesh", label = "Mesh LOD 0", isResource = true)
    public String lod0;
    @MutableField(group = "Mesh", label = "Use LOD 0 when distance")
    public float lod0DistanceUntil = 50;
    @MutableField(group = "Mesh", label = "Mesh LOD 1", isResource = true)
    public String lod1;
    @MutableField(group = "Mesh", label = "Use LOD 1 when distance")
    public float lod1DistanceUntil = 100;
    @MutableField(group = "Mesh", label = "Mesh LOD 2", isResource = true)
    public String lod2;
    @MutableField(group = "Mesh", label = "Use LOD 2 when distance")
    public float lod2DistanceUntil = 150;
    @MutableField(group = "Mesh", label = "Mesh LOD 3", isResource = true)
    public String lod3;
    @MutableField(group = "Mesh", label = "Use LOD 3 when distance")
    public float lod3DistanceUntil = 200;
    @MutableField(group = "Mesh", label = "Mesh LOD 4", isResource = true)
    public String lod4;

    // INSTANCING
    @MutableField(group = "Instancing", label = "Enable instancing")
    public boolean isInstancedRendering = false;
    @MutableField(group = "Instancing", label = "Number of instances", min = 1)
    public int numberOfInstances = 10;
    @MutableField(group = "Instancing", label = "Scene members")
    public List<Transformation> primitives = new ArrayList<>();

    public transient RenderingRequest renderRequest;

    // CULLING
    @MutableField(group = "Culling", label = "Enable culling")
    public boolean isCullingEnabled = true;
    @MutableField(group = "Culling", label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 200;
    @MutableField(group = "Culling", label = "Bounding box size")
    public final Vector3f boundingBoxSize = new Vector3f(1);


    public float distanceFromCamera = 0f;

    public MeshComponent(Entity entity) {
        super(entity);
    }


    @Override
    public ComponentType getType() {
        return ComponentType.MESH;
    }
}
