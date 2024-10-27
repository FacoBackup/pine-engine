package com.pine.service.streaming.impl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

public enum CubeMapFace {
    POSITIVE_X(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f)),
    NEGATIVE_X(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f)),
    POSITIVE_Y(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
    NEGATIVE_Y(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 1.0f, 0.0f)),
    POSITIVE_Z(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f)),
    NEGATIVE_Z(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));


    private static final float FOV_Y = (float) (Math.PI / 2f);
    private static final float ASPECT_RATIO = 1f;
    private static final float Z_NEAR = .1f;
    private static final float Z_FAR = 10000f;

    public static final Matrix4f invProjection = new Matrix4f();
    public static final Matrix4f projection = new Matrix4f();

    static {
        projection.setPerspective(FOV_Y, ASPECT_RATIO, Z_NEAR, Z_FAR);
        projection.invert(invProjection);
    }

    private final int glFace;
    private final Vector3f up;
    private final Vector3f target;

    CubeMapFace(int glFace, Vector3f up, Vector3f target) {
        this.glFace = glFace;
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
