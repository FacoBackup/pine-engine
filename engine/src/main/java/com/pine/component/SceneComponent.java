package com.pine.component;

import com.pine.PBean;
import com.pine.component.rendering.CompositeScene;
import com.pine.inspection.MutableField;
import com.pine.repository.rendering.RuntimeDrawDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@PBean
public class SceneComponent extends AbstractComponent<SceneComponent> {
    @MutableField(label = "Scene members")
    public final CompositeScene compositeScene = new CompositeScene(false);
    public final transient List<RuntimeDrawDTO> requests = new ArrayList<>();

    public SceneComponent(Integer entityId) {
        super(entityId);
    }

    public SceneComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class, CullingComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Scene";
    }
}
