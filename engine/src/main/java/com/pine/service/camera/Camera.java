package com.pine.service.camera;


import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.UUID;

public class Camera implements Serializable {
    public final String id = UUID.randomUUID().toString();
    public final Vector3f position = new Vector3f();
    public final Matrix4f viewMatrix = new Matrix4f();
    public final Matrix4f projectionMatrix = new Matrix4f();
    public final Matrix4f invViewMatrix = new Matrix4f();
    public final Matrix4f invProjectionMatrix = new Matrix4f();
    public final Matrix4f viewProjectionMatrix = new Matrix4f();
    public final Matrix4f staticViewMatrix = new Matrix4f();
    public final Matrix4f skyboxProjectionMatrix = new Matrix4f();
    public final Matrix4f invSkyboxProjectionMatrix = new Matrix4f();
    public final Vector3f translationBuffer = new Vector3f();
    public final Quaternionf rotationBuffer = new Quaternionf();

    public boolean isOrthographic = false;

    public float zFar = 5000;
    public float zNear = .1f;
    public float fov = (float) Math.toRadians(60);
    public float aspectRatio = 1;
    public float orthographicProjectionSize = 50;

}

