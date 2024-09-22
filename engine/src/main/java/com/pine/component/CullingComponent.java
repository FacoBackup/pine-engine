package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import org.joml.Vector3f;

import java.util.Set;

@PBean
public class CullingComponent extends AbstractComponent<CullingComponent> {

    @MutableField(label = "Max distance from camera")
    public int maxDistanceFromCamera = 300;
    @MutableField(label = "Frustum box size")
    public final Vector3f frustumBoxDimensions = new Vector3f(1);

    public CullingComponent(Integer entityId) {
        super(entityId);
    }

    public CullingComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Culling";
    }
}
