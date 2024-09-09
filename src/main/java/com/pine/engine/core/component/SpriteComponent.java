package com.pine.engine.core.component;

import java.util.List;

public class SpriteComponent extends AbstractComponent{
    private String imageID;
    private final int[] attributes = {0, 0};

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }

    public boolean isAlwaysFaceCamera() {
        return attributes[0] == 1;
    }

    public void setAlwaysFaceCamera(boolean alwaysFaceCamera) {
        this.attributes[0] = alwaysFaceCamera ? 1 : 0;
    }

    public boolean isKeepSameSize() {
        return attributes[1] == 1;
    }

    public void setKeepSameSize(boolean keepSameSize) {
        this.attributes[1] = keepSameSize ? 1 : 0;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }
}
