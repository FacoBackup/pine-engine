package com.pine.service.svo;

import org.joml.Vector3f;

import java.io.Serializable;

public class BoundingBox implements Serializable {
    public Vector3f min = new Vector3f(Float.MAX_VALUE);
    public Vector3f max = new Vector3f(Float.MIN_VALUE);

    public boolean intersects(BoundingBox bb) {
        return (min.x < bb.max.x && max.x > bb.min.x &&
                min.y < bb.max.y && max.y > bb.min.y &&
                min.z < bb.max.z && max.z > bb.min.z);
    }

    public boolean intersects(Vector3f point) {
        return (min.x < point.x && max.x >= point.x &&
                min.y < point.y && max.y >= point.y &&
                min.z < point.z && max.z >= point.z);
    }
}
