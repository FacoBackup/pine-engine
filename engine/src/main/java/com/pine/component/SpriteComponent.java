package com.pine.component;

import com.pine.injection.EngineInjectable;

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
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }


    @Override
    public String getComponentName() {
        return "Sprite";
    }
}
