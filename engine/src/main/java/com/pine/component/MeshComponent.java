package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.theme.Icons;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@PBean
public class MeshComponent extends AbstractComponent<MeshComponent> {
    @MutableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @MutableField(label = "Mesh LOD 0")
    public MeshStreamableResource lod0;
    public float lod0DistanceUntil = 100;

    @MutableField(label = "Mesh LOD 1")
    public MeshStreamableResource lod1;

    @MutableField(label = "Use LOD 1 if distance from camera is smaller than")
    public float lod1DistanceUntil = 200;

    @MutableField(label = "Mesh LOD 2")
    public MeshStreamableResource lod2;

    @MutableField(label = "Use LOD 2 if distance from camera is smaller than")
    public float lod2DistanceUntil = 300;

    @MutableField(label = "Mesh LOD 3")
    public MeshStreamableResource lod3;

    @MutableField(label = "Use LOD 3 if distance from camera is smaller than")
    public float lod3DistanceUntil = 300;

    @MutableField(label = "Mesh LOD 4")
    public MeshStreamableResource lod4;


    // INSTANCING
    @MutableField(label = "Enable instancing")
    public boolean isInstancedRendering = false;

    @MutableField(label = "Number of instances", min = 1)
    public int numberOfInstances = 10;

    public transient RenderingRequest renderRequest;

    @MutableField(label = "Scene members")
    public List<Transformation> primitives = new ArrayList<>();


    // CULLING
    @MutableField(label = "Enable culling")
    public boolean isCullingEnabled = true;

    @MutableField(label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 300;

    @MutableField(label = "Frustum box size")
    public final Vector3f frustumBoxDimensions = new Vector3f(1);

    public float distanceFromCamera = 0f;

    public MeshComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public MeshComponent() {}

    @Override
    public String getTitle() {
        return "Mesh Component";
    }

    @Override
    public String getIcon() {
        return Icons.view_in_ar;
    }
}
