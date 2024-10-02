package com.pine.repository.rendering;

import com.pine.component.Transformation;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.primitives.mesh.Primitive;

import java.util.Collections;
import java.util.List;

public class PrimitiveRenderRequest {
    public Primitive primitive;
    public final MeshRuntimeData runtimeData;
    // TODO - MATERIAL

    public final Transformation transformation;
    public List<Transformation> transformations;
    public int renderIndex;

    public PrimitiveRenderRequest(Primitive primitive, MeshRuntimeData runtimeData, Transformation transformation) {
        this(primitive, runtimeData, transformation, Collections.emptyList());
    }

    public PrimitiveRenderRequest(Primitive primitive, MeshRuntimeData runtimeData, Transformation transformation, List<Transformation> transformations) {
        this.primitive = primitive;
        this.runtimeData = runtimeData;
        this.transformation = transformation;
        this.transformations = transformations;
    }
}
