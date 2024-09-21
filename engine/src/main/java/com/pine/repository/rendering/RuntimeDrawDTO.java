package com.pine.repository.rendering;

import com.pine.component.rendering.SimpleTransformation;
import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import org.joml.Vector3f;

public class RuntimeDrawDTO {
    public MeshPrimitiveResource primitive;
    public final MeshRuntimeData runtimeData;
    // TODO - MATERIAL

    public final SimpleTransformation transformation;


    public RuntimeDrawDTO(MeshPrimitiveResource primitive, MeshRuntimeData runtimeData, SimpleTransformation transformation) {
        this.primitive = primitive;
        this.runtimeData = runtimeData;
        this.transformation = transformation;
    }
}
