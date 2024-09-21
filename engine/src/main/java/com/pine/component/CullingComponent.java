package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;

import java.util.Set;

@PBean
public class CullingComponent extends AbstractComponent<CullingComponent> {

    @MutableField(label = "Max distance from camera")
    public long maxDistanceFromCamera = 300;
    @MutableField(label = "Frustum culling box width")
    public long frustumCullingBoxWidth = 300;
    @MutableField(label = "Frustum culling box height")
    public long frustumCullingBoxHeight = 300;

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
