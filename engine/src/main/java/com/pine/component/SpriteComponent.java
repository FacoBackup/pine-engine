package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.service.resource.primitives.texture.TextureResource;

import java.util.Set;

@PBean
public class SpriteComponent extends AbstractComponent<SpriteComponent> {
    @MutableField(label = "Texture")
    public TextureResource texture;
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
