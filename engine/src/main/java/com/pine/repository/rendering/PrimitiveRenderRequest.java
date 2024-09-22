package com.pine.repository.rendering;

import com.pine.component.rendering.SimpleTransformation;
import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;

import java.util.Collections;
import java.util.List;

public class PrimitiveRenderRequest {
    public MeshPrimitiveResource primitive;
    public final MeshRuntimeData runtimeData;
    // TODO - MATERIAL

    public final SimpleTransformation transformation;
    public final List<SimpleTransformation> transformations;


    public PrimitiveRenderRequest(MeshPrimitiveResource primitive, MeshRuntimeData runtimeData, List<SimpleTransformation> transformations) {
        this(primitive, runtimeData, new SimpleTransformation(), transformations);
    }
    public PrimitiveRenderRequest(MeshPrimitiveResource primitive, MeshRuntimeData runtimeData, SimpleTransformation transformation) {
        this(primitive, runtimeData, transformation, Collections.emptyList());
    }

    public PrimitiveRenderRequest(MeshPrimitiveResource primitive, MeshRuntimeData runtimeData, SimpleTransformation transformation, List<SimpleTransformation> transformations) {
        this.primitive = primitive;
        this.runtimeData = runtimeData;
        this.transformation = transformation;
        this.transformations = transformations;
    }
}
