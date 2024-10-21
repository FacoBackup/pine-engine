package com.pine.component;

import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.ref.TextureResourceRef;


public class SpriteComponent extends AbstractComponent {
    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Texture")
    public String texture;

    public transient TextureResourceRef textureRef;

    @InspectableField(label = "Keep same size")
    public boolean keepSameSize = true;

    @InspectableField(label = "Always face camera")
    public boolean alwaysFaceCamera = true;

    public SpriteComponent(Entity entity) {
        super(entity);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SPRITE;
    }
}
