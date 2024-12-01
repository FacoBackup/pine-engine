package com.pine.engine.service.streaming.impl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import static com.pine.engine.Engine.PI_OVER_2;

public enum CubeMapFace {

    POSITIVE_X(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X, -PI_OVER_2, 0, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f)),
    NEGATIVE_X(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, PI_OVER_2, 0, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f)),
    POSITIVE_Y(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, -PI_OVER_2, new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 1.0f, 0.0f)),
    NEGATIVE_Y(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, PI_OVER_2, new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
    POSITIVE_Z(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f)),
    NEGATIVE_Z(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, PI_OVER_2 * 2f, 0, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));

    private final int glFace;
    public final float yaw;
    public final float pitch;
    private final Vector3f up;
    private final Vector3f target;

    private static final float FOV_Y = PI_OVER_2;
    private static final float ASPECT_RATIO = 1f;
    private static final float Z_NEAR = .1f;
    private static final float Z_FAR = 10000f;

    public static final Matrix4f projection = new Matrix4f();

    static {
        projection.setPerspective(FOV_Y, ASPECT_RATIO, Z_NEAR, Z_FAR);
    }

    CubeMapFace(int glFace, float yaw, float pitch, Vector3f up, Vector3f target) {
        this.glFace = glFace;
        this.yaw = yaw;
        this.pitch = pitch;
        this.up = up;
        this.target = target;
    }

    public int getGlFace() {
        return glFace;
    }

    public Vector3f getTarget() {
        return target;
    }

    public Vector3f getUp() {
        return up;
    }

    public static Matrix4f createViewMatrixForFace(CubeMapFace face, Vector3f cameraPosition) {
        return new Matrix4f().lookAt(cameraPosition, face.getTarget(), face.getUp());
    }
}
