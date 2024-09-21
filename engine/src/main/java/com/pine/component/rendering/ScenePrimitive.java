package com.pine.component.rendering;

import com.pine.inspection.ResourceRef;
import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;

public class ScenePrimitive {
    public boolean castsShadows = true;
    public boolean contributeToProbes = true;
    public ResourceRef<MeshPrimitiveResource> primitive;

    public final SimpleTransformation transformation = new SimpleTransformation();

    public int maxDistanceFromCamera = 300;
    public int frustumCullingBoxWidth = 1;
    public int frustumCullingBoxHeight = 1;

    public boolean isCulled = false;
}
