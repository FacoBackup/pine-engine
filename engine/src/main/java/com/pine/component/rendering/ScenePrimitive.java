package com.pine.component.rendering;

import com.pine.inspection.ResourceRef;
import com.pine.service.resource.primitives.mesh.Primitive;

public class ScenePrimitive {
    public boolean castsShadows = true;
    public boolean contributeToProbes = true;
    public ResourceRef<Primitive> primitive;

    public final SimpleTransformation transformation;

    public int maxDistanceFromCamera = 300;
    public int frustumCullingBoxWidth = 1;
    public int frustumCullingBoxHeight = 1;

    public ScenePrimitive(SimpleTransformation transformation) {
        this.transformation = transformation;
    }
}
