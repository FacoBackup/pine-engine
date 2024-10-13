package com.pine.repository.rendering;

import com.pine.component.Transformation;
import com.pine.service.streaming.material.MaterialStreamableResource;
import com.pine.service.streaming.mesh.MeshStreamableResource;
import com.pine.type.MaterialRenderingMode;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class RenderingRequest implements Serializable {
    public MeshStreamableResource mesh;
    public MaterialStreamableResource material;

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
