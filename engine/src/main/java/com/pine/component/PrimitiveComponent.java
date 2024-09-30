package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.service.resource.primitives.mesh.Primitive;
import com.pine.service.resource.resource.ResourceType;
import com.pine.theme.Icons;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.Set;

@PBean
public class PrimitiveComponent extends AbstractComponent<PrimitiveComponent> {
    @MutableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @ResourceTypeField(type = ResourceType.PRIMITIVE)
    @MutableField(label = "Primitive")
    public ResourceRef<Primitive> primitive;

    @MutableField(label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 300;

    @MutableField(label = "Frustum box size")
    public final Vector3f frustumBoxDimensions = new Vector3f(1);

    public PrimitiveComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public PrimitiveComponent() {}

    @Override
    public Set<Class<? extends EntityComponent>> getDependencies() {
        return Set.of(CullingComponent.class);
    }

    @Override
    public String getTitle() {
        return "Primitive";
    }

    @Override
    public String getIcon() {
        return Icons.view_in_ar;
    }
}
