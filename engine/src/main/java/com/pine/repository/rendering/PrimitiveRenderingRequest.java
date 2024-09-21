package com.pine.repository.rendering;

import com.pine.component.rendering.SimpleTransformation;
import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;

public class PrimitiveRenderingRequest {
    public MeshPrimitiveResource primitive;
    public final MeshRuntimeData runtimeData;
    // TODO - MATERIAL

    public final SimpleTransformation transformation;


    public PrimitiveRenderingRequest(MeshPrimitiveResource primitive, MeshRuntimeData runtimeData, SimpleTransformation transformation) {
        this.primitive = primitive;
        this.runtimeData = runtimeData;
        this.transformation = transformation;
    }
}
