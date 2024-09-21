package com.pine.component;

import com.pine.injection.EngineInjectable;
import com.pine.inspection.MutableField;
import com.pine.service.resource.primitives.texture.Texture;

import java.util.Set;

@EngineInjectable
public class SpriteComponent extends AbstractComponent<SpriteComponent> {
    @MutableField(label = "Texture")
    public Texture texture;
    @MutableField(label = "Keep same size")
    public boolean keepSameSize = true;
    @MutableField(label = "Always face camera")
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
