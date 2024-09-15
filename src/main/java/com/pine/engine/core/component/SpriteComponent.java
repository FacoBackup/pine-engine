package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;

import java.util.List;
import java.util.Set;

@EngineInjectable
public class SpriteComponent extends AbstractComponent<SpriteComponent> {
    public String textureId;
    public boolean keepSameSize = true;
    public boolean alwaysFaceCamera = true;

    public SpriteComponent(Integer entityId) {
        super(entityId);
    }

    public SpriteComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }
}
