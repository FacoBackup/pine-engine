package com.pine.service.camera;

import org.joml.Vector3f;

public class Plane {
    public Vector3f normal = new Vector3f();
    public float distance;

    public void normalize() {
        float length = normal.length();
        normal.div(length);
        distance /= length;
    }

    public float distanceToPoint(Vector3f point) {
        return normal.dot(point) + distance;
    }
}