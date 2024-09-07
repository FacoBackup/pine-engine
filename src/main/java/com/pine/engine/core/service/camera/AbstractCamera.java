package com.pine.engine.core.service.camera;


import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;

public abstract class AbstractCamera {
    private final String id = UUID.randomUUID().toString();
    private final Vector3f tmpVec = new Vector3f();
    protected final Vector3f position = new Vector3f();
    protected final Vector3f direction = new Vector3f(0, 0, -1);
    protected final Vector3f up = new Vector3f(0, 1, 0);
    protected final Matrix4f projection = new Matrix4f();
    protected final Matrix4f view = new Matrix4f();
    protected final Matrix4f combined = new Matrix4f();
    protected final Matrix4f invProjectionView = new Matrix4f();
    protected float near = 1;
    protected float far = 100;
    protected float viewportWidth = 0;
    protected float viewportHeight = 0;

    public String getId() {
        return id;
    }

    public abstract void tick();

    public void lookAt(float x, float y, float z) {
        tmpVec.set(x, y, z).sub(position).normalize();
        if (tmpVec.length() > 0) {
            float dot = tmpVec.dot(up);
            if (Math.abs(dot - 1) < 0.000000001f) {
                up.set(direction).mul(-1);
            } else if (Math.abs(dot + 1) < 0.000000001f) {
                up.set(direction);
            }
            direction.set(tmpVec);
            normalizeUp();
        }
    }

    public void lookAt(Vector3f target) {
        lookAt(target.x, target.y, target.z);
    }

    public void normalizeUp() {
        tmpVec.set(direction).cross(up);
        up.set(tmpVec).cross(direction).normalize();
    }

    public void rotateAround(Vector3f point, Vector3f axis, float angle) {
        tmpVec.set(point);
        tmpVec.sub(position);
        translate(tmpVec);
        rotate(axis, angle, direction);
        rotate(axis, angle, up);
        rotate(axis, angle, tmpVec);
        translate(-tmpVec.x, -tmpVec.y, -tmpVec.z);
    }

    private static void rotate(Vector3f axis, float angle, Vector3f vec) {
        if (axis.x > 0) {
            vec.rotateX(angle);
        }
        if (axis.y > 0) {
            vec.rotateY(angle);
        }
        if (axis.z > 0) {
            vec.rotateZ(angle);
        }
    }

    public void translate(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void translate(Vector3f vec) {
        position.add(vec);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public Vector3f getUp() {
        return up;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Matrix4f getView() {
        return view;
    }

    public Matrix4f getCombined() {
        return combined;
    }

    public Matrix4f getInvProjectionView() {
        return invProjectionView;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public void setViewportHeight(float viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public void setViewportWidth(float viewportWidth) {
        this.viewportWidth = viewportWidth;
    }
}

