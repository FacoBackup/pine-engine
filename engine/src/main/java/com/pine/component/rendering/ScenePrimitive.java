package com.pine.component.rendering;

import com.pine.component.ResourceRef;
import com.pine.inspection.MutableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.inspection.Inspectable;
import com.pine.service.resource.primitives.mesh.Primitive;
import com.pine.service.resource.resource.ResourceType;
import com.pine.theme.Icons;
import org.joml.Vector3f;

public class ScenePrimitive extends Inspectable {

    @MutableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @ResourceTypeField(type = ResourceType.PRIMITIVE)
    @MutableField(label = "Primitive")
    public ResourceRef<Primitive> primitive;

    @MutableField
    public final SimpleTransformation transformation;

    @MutableField(label = "Max distance from camera", min = 1, max = Integer.MAX_VALUE, isAngle = false, isDirectChange = false)
    public int maxDistanceFromCamera = 300;

    @MutableField(label = "Frustum box size")
    public final Vector3f frustumBoxDimensions = new Vector3f(1);

    public ScenePrimitive(SimpleTransformation transformation) {
        this.transformation = transformation;
    }

    @Override
    public String getTitle() {
        return "Scene Primitive";
    }

    @Override
    public String getIcon() {
        return Icons.view_in_ar;
    }
}
