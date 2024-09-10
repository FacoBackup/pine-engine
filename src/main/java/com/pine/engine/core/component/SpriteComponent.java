package com.pine.engine.core.component;

import java.util.List;

public class SpriteComponent extends AbstractComponent {
    public String imageID;
    public final int[] attributes = {0, 0};
    public boolean keepSameSize = true;
    public boolean alwaysFaceCamera = true;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }
}
