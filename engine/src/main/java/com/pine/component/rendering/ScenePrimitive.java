package com.pine.component.rendering;

import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class ScenePrimitive {
    public boolean castsShadows = true;
    public boolean contributeToProbes = true;
    public MeshPrimitiveResource primitive;

    public final SimpleTransformation transformation = new SimpleTransformation();

    public long maxDistanceFromCamera = 300;
    public long frustumCullingBoxWidth = 1;
    public long frustumCullingBoxHeight = 1;
}
