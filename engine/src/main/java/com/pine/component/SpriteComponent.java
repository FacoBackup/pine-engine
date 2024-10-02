package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.service.resource.primitives.texture.TextureResource;
import com.pine.service.resource.resource.ResourceType;
import com.pine.theme.Icons;

import java.util.LinkedList;

@PBean
public class SpriteComponent extends AbstractComponent<SpriteComponent> {
    @ResourceTypeField(type = ResourceType.TEXTURE)
    @MutableField(label = "Texture")
    public ResourceRef<TextureResource> heightMapTexture;

    @MutableField(label = "Keep same size")
    public boolean keepSameSize = true;

    @MutableField(label = "Always face camera")
    public boolean alwaysFaceCamera = true;

    public SpriteComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public SpriteComponent() {}

    @Override
    public String getTitle() {
        return "Sprite";
    }

    @Override
    public String getIcon() {
        return Icons.image;
    }
}
