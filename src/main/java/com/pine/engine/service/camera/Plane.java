package com.pine.engine.service.camera;

import org.joml.Vector3f;

import java.io.Serializable;

public class Plane implements Serializable {
    public Vector3f normal;
    public float d;

    public Plane(Vector3f normal, float d) {
        this.normal = normal;
        this.d = d;
    }

    public float distanceToPoint(Vector3f point) {
        return normal.x * point.x + normal.y * point.y + normal.z * point.z + d;
    }
}