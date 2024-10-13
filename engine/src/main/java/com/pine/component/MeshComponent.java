package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.streaming.mesh.MeshStreamableResource;
import com.pine.service.streaming.texture.TextureStreamableResource;
import com.pine.theme.Icons;
import com.pine.type.MaterialRenderingMode;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MeshComponent extends AbstractComponent {
    @MutableField(label = "Casts shadow")
    public boolean castsShadows = true;
    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @MutableField(group = "Mesh", label = "Mesh LOD 0")
    public MeshStreamableResource lod0;
    @MutableField(group = "Mesh", label = "Use LOD 0 when distance")
    public float lod0DistanceUntil = 50;
    @MutableField(group = "Mesh", label = "Mesh LOD 1")
    public MeshStreamableResource lod1;
    @MutableField(group = "Mesh", label = "Use LOD 1 when distance")
    public float lod1DistanceUntil = 100;
    @MutableField(group = "Mesh", label = "Mesh LOD 2")
    public MeshStreamableResource lod2;
    @MutableField(group = "Mesh", label = "Use LOD 2 when distance")
    public float lod2DistanceUntil = 150;
    @MutableField(group = "Mesh", label = "Mesh LOD 3")
    public MeshStreamableResource lod3;
    @MutableField(group = "Mesh", label = "Use LOD 3 when distance")
    public float lod3DistanceUntil = 200;
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
    public int maxDistanceFromCamera = 200;
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
    @MutableField(group = "Material", label = "use parallax")
    public boolean useParallax = false;
    @MutableField(group = "Material", label = "Parallax height scale")
    public float parallaxHeightScale = 1;
    @MutableField(group = "Material", label = "Parallax layers")
    public int parallaxLayers = 16;
    @MutableField(group = "Material", label = "Rendering mode")
    public MaterialRenderingMode renderingMode = MaterialRenderingMode.ISOTROPIC;
    @MutableField(group = "Material", label = "Screen space reflections")
    public boolean ssrEnabled;

    @MutableField(group = "Material", label = "Anisotropic rotation")
    public float anisotropicRotation;
    @MutableField(group = "Material", label = "Clear coat")
    public float clearCoat;
    @MutableField(group = "Material", label = "Anisotropy")
    public float anisotropy;
    @MutableField(group = "Material", label = "Sheen")
    public float sheen;
    @MutableField(group = "Material", label = "Sheen tint")
    public float sheenTint;

    public float distanceFromCamera = 0f;

    public MeshComponent(Entity entity) {
        super(entity);
    }


    @Override
    public ComponentType getType() {
        return ComponentType.MESH;
    }
}
