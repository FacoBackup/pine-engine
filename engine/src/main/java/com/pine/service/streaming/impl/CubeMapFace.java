package com.pine.service.streaming.impl;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

public enum CubeMapFace {
    POSITIVE_X(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f)),
    NEGATIVE_X(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f)),
    POSITIVE_Y(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 1.0f, 0.0f)),
    NEGATIVE_Y(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
    POSITIVE_Z(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f)),
    NEGATIVE_Z(GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));

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
}
