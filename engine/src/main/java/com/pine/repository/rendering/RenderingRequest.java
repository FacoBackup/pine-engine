package com.pine.repository.rendering;

import com.pine.component.Transformation;
import com.pine.repository.streaming.MeshStreamableResource;

import java.util.Collections;
import java.util.List;

public class RenderingRequest {
    public MeshStreamableResource mesh;
    // TODO - MATERIAL

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
