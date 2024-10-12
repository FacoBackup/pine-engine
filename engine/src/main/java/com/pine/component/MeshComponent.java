package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.repository.streaming.TextureStreamableResource;
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

    @MutableField(group = "Mesh", label = "Mesh LOD 0")
    public MeshStreamableResource lod0;
    @MutableField(group = "Mesh", label = "Use LOD 0 when distance")
    public float lod0DistanceUntil = 100;
    @MutableField(group = "Mesh", label = "Mesh LOD 1")
    public MeshStreamableResource lod1;
    @MutableField(group = "Mesh", label = "Use LOD 1 when distance")
    public float lod1DistanceUntil = 200;
    @MutableField(group = "Mesh", label = "Mesh LOD 2")
    public MeshStreamableResource lod2;
    @MutableField(group = "Mesh", label = "Use LOD 2 when distance")
    public float lod2DistanceUntil = 300;
    @MutableField(group = "Mesh", label = "Mesh LOD 3")
    public MeshStreamableResource lod3;
    @MutableField(group = "Mesh", label = "Use LOD 3 when distance")
    public float lod3DistanceUntil = 300;
    @MutableField(group = "Mesh", label = "Mesh LOD 4")
    public MeshStreamableResource lod4;


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
    public int maxDistanceFromCamera = 300;
    @MutableField(group = "Culling", label = "Bounding box size")
    public final Vector3f boundingBoxSize = new Vector3f(1);


    // MATERIAL
    @MutableField(group = "Material", label = "Albedo")
    public TextureStreamableResource albedo;
    @MutableField(group = "Material", label = "Roughness")
    public TextureStreamableResource roughness;
    @MutableField(group = "Material", label = "Metallic")
    public TextureStreamableResource metallic;
    @MutableField(group = "Material", label = "Ambient occlusion")
    public TextureStreamableResource ao;
    @MutableField(group = "Material", label = "Normal")
    public TextureStreamableResource normal;
    @MutableField(group = "Material", label = "Height map")
    public TextureStreamableResource heightMap;
    @MutableField(group = "Material", label = "Material mask", help = "R isEmission | G useSSR | B useGI | A useAO")
    public TextureStreamableResource materialMask;
    @MutableField(group = "Material", label = "use parallax")
    public boolean useParallax = false;
    @MutableField(group = "Material", label = "Parallax height scale")
    public float parallaxHeightScale = 1;
    @MutableField(group = "Material", label = "Parallax layers")
    public int parallaxLayers = 16;

    public float distanceFromCamera = 0f;

    public MeshComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public MeshComponent() {
    }

    @Override
    public String getTitle() {
        return "Mesh Component";
    }

    @Override
    public String getIcon() {
        return Icons.view_in_ar;
    }
}
