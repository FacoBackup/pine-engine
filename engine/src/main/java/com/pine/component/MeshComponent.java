package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.theme.Icons;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.Set;

@PBean
public class MeshComponent extends AbstractComponent<MeshComponent> {
    @MutableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @MutableField(label = "Primitive")
    public MeshStreamableResource primitive;

    @MutableField(label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 300;

    @MutableField(label = "Frustum box size")
    public final Vector3f frustumBoxDimensions = new Vector3f(1);

    public MeshComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public MeshComponent() {}

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
