package com.pine.engine.service.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.UUID;

public class OCamera extends OrthographicCamera implements ICamera {
    private final String id = UUID.randomUUID().toString();

    @Override
    public String getId() {
        return id;
    }
}
