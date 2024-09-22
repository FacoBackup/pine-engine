package com.pine.service.camera;


import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;

public class Camera {
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
    public boolean cameraMotionBlur = false;
    public boolean bloom = false;
    public boolean filmGrain = false;
    public boolean vignetteEnabled = false;
    public boolean chromaticAberration = false;
    public boolean distortion = false;
    public boolean DOF = false;
    public int size = 50;
    public int focusDistanceDOF = 10;
    public double apertureDOF = 1.2;
    public int focalLengthDOF = 5;
    public int samplesDOF = 100;
    public double filmGrainStrength = 1.;
    public double vignetteStrength = .25;
    public double bloomThreshold = .75;
    public int bloomQuality = 8;
    public int bloomOffset = 0;
    public double gamma = 2.2;
    public double exposure = 1.;
    public int chromaticAberrationStrength = 1;
    public int distortionStrength = 1;

    public float zFar = 5000;
    public float zNear = .1f;
    public float fov = (float) Math.toRadians(60);
    public float aspectRatio = 1;
    public float orthographicProjectionSize = 50;

}

