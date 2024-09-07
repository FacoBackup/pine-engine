package com.pine.engine.service.camera;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import java.util.UUID;

public class PCamera extends PerspectiveCamera implements ICamera {
    private final String id = UUID.randomUUID().toString();

    @Override
    public String getId() {
        return id;
    }
}
