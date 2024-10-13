package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.service.streaming.texture.TextureStreamableResource;
import com.pine.theme.Icons;

import java.util.LinkedList;


public class SpriteComponent extends AbstractComponent {
    @MutableField(label = "Texture")
    public TextureStreamableResource heightMapTexture;

    @MutableField(label = "Keep same size")
    public boolean keepSameSize = true;

    @MutableField(label = "Always face camera")
    public boolean alwaysFaceCamera = true;

    public SpriteComponent(Entity entity) {
        super(entity);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SPRITE;
    }
}
