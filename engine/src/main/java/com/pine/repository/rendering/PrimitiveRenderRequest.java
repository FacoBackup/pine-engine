package com.pine.repository.rendering;

import com.pine.component.rendering.SimpleTransformation;
import com.pine.service.resource.primitives.mesh.Primitive;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;

import java.util.Collections;
import java.util.List;

public class PrimitiveRenderRequest {
    public Primitive primitive;
    public final MeshRuntimeData runtimeData;
    // TODO - MATERIAL

    public final SimpleTransformation transformation;
    public final List<SimpleTransformation> transformations;


    public PrimitiveRenderRequest(Primitive primitive, MeshRuntimeData runtimeData, List<SimpleTransformation> transformations) {
        this(primitive, runtimeData, new SimpleTransformation(), transformations);
    }
    public PrimitiveRenderRequest(Primitive primitive, MeshRuntimeData runtimeData, SimpleTransformation transformation) {
        this(primitive, runtimeData, transformation, Collections.emptyList());
    }

    public PrimitiveRenderRequest(Primitive primitive, MeshRuntimeData runtimeData, SimpleTransformation transformation, List<SimpleTransformation> transformations) {
        this.primitive = primitive;
        this.runtimeData = runtimeData;
        this.transformation = transformation;
        this.transformations = transformations;
    }
}
