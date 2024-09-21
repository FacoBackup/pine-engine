package com.pine.component.rendering;

import org.joml.Vector3f;

public class SimpleTransformation {
    public Vector3f translation = new Vector3f();
    public Vector3f rotation = new Vector3f();
    public Vector3f scale = new Vector3f(1);
    public int parentTransformationId = -1;
}
