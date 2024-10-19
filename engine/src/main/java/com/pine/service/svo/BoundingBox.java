package com.pine.service.svo;

import org.joml.Vector3f;

public class BoundingBox {
    public Vector3f min = new Vector3f(Float.MAX_VALUE);
    public Vector3f max = new Vector3f(Float.MIN_VALUE);
}
