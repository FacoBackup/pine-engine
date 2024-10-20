package com.pine.service.svo;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Triangle {
    public final Vector3f v0;
    public final Vector3f v1;
    public final Vector3f v2;

    public final Vector2f uv0 = new Vector2f();
    public final Vector2f uv1 = new Vector2f();
    public final Vector2f uv2 = new Vector2f();

    public Triangle(Vector3f v0, Vector3f v1, Vector3f v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }
}
