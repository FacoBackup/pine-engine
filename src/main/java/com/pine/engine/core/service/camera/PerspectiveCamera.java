package com.pine.engine.core.service.camera;

import org.joml.Vector3f;

public class PerspectiveCamera extends AbstractCamera {
    private float fieldOfView = 67;
    private final Vector3f tmp = new Vector3f();

    @Override
    public void tick() {
        projectionMatrix.perspective(fieldOfView, viewportWidth / viewportHeight, Math.abs(near), Math.abs(far));
        viewMatrix.lookAt(position, tmp.set(position).add(direction), up);
        updateMatrices();
    }

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }
}
