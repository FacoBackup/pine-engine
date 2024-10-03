package com.pine.repository.rendering;

import com.pine.component.Transformation;
import com.pine.service.resource.primitives.mesh.Mesh;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;

import java.util.Collections;
import java.util.List;

public class PrimitiveRenderRequest {
    public Mesh mesh;
    public final MeshRuntimeData runtimeData;
    // TODO - MATERIAL

    public final Transformation transformation;
    public List<Transformation> transformations;
    public int renderIndex;

    public PrimitiveRenderRequest(Mesh mesh, MeshRuntimeData runtimeData, Transformation transformation) {
        this(mesh, runtimeData, transformation, Collections.emptyList());
    }

    public PrimitiveRenderRequest(Mesh mesh, MeshRuntimeData runtimeData, Transformation transformation, List<Transformation> transformations) {
        this.mesh = mesh;
        this.runtimeData = runtimeData;
        this.transformation = transformation;
        this.transformations = transformations;
    }
}
