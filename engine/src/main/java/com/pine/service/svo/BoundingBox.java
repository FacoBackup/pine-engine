package com.pine.service.svo;

import org.joml.Vector3f;

import java.io.Serializable;

public class BoundingBox implements Serializable {
    public Vector3f min = new Vector3f(Float.MAX_VALUE);
    public Vector3f max = new Vector3f(Float.MIN_VALUE);

    public boolean intersects(BoundingBox bb) {
        return (bb.min.x < max.x && bb.max.x > min.x &&
                bb.min.y < max.y && bb.max.y > min.y &&
                bb.min.z < max.z && bb.max.z > min.z);
    }
}
