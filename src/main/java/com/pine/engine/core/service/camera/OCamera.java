package com.pine.engine.core.service.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.UUID;

public class OCamera extends OrthographicCamera implements ICamera {
    private final String id = UUID.randomUUID().toString();

    @Override
    public String getId() {
        return id;
    }
}
