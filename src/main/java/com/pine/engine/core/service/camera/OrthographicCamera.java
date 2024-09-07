package com.pine.engine.core.service.camera;

import org.joml.*;

public class OrthographicCamera extends AbstractCamera {
    public float zoom = 1;

    private final Vector3f tmp = new Vector3f();

    @Override
    public void tick() {
        projection.ortho(zoom * -viewportWidth / 2, zoom * (viewportWidth / 2), zoom * -(viewportHeight / 2),
                zoom * viewportHeight / 2, near, far);
        view.lookAt(position, tmp.set(position).add(direction), up);
        combined.set(projection);
        combined.mul(view);

        invProjectionView.set(combined);
        invProjectionView.invert();
    }

    public void translate(float x, float y) {
        translate(x, y, 0);
    }

    public void translate(Vector2f vec) {
        translate(vec.x, vec.y, 0);
    }
}
