package com.pine.repository.rendering;

import com.pine.component.rendering.SimpleTransformation;
import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import org.joml.Vector3f;

import java.util.List;

public class CompositeDrawDTO extends RuntimeDrawDTO{
    public CompositeDrawDTO(MeshPrimitiveResource primitive, MeshRuntimeData runtimeData, List<SimpleTransformation> transformations) {
        super(primitive, runtimeData, new SimpleTransformation());
    }
}
