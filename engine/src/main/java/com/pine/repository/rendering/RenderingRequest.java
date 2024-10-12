package com.pine.repository.rendering;

import com.pine.component.MeshComponent;
import com.pine.component.Transformation;
import com.pine.repository.streaming.MeshStreamableResource;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class RenderingRequest implements Serializable {
    public MeshStreamableResource mesh;

    public int albedo;
    public int roughness;
    public int metallic;
    public int ao;
    public int normal;
    public int heightMap;
    public int materialMask;
    public float parallaxHeightScale;
    public int parallaxLayers;
    public boolean useParallax;

    public final Transformation transformation;
    public List<Transformation> transformations;
    public int renderIndex;

    public RenderingRequest(MeshStreamableResource mesh, Transformation transformation) {
        this(mesh, transformation, Collections.emptyList());
    }

    public RenderingRequest(MeshStreamableResource mesh, Transformation transformation, List<Transformation> transformations) {
        this.mesh = mesh;
        this.transformation = transformation;
        this.transformations = transformations;
    }
}
