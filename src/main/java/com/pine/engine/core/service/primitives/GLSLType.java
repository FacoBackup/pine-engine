package com.pine.engine.core.service.primitives;

import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL46;

public enum GLSLType {
    vec2("vec2", GL46.GL_FLOAT_VEC2, new int[]{8, 8}),
    vec3("vec3", GL46.GL_FLOAT_VEC3, new int[]{16, 12}),
    vec4("vec4", GL46.GL_FLOAT_VEC4, new int[]{16, 16}),
    mat3("mat3", GL46.GL_FLOAT_MAT3, new int[]{48, 48}),
    mat4("mat4", GL46.GL_FLOAT_MAT4, new int[]{64, 64}),
    f("float", GL46.GL_FLOAT, new int[]{4, 4}),
    i("int", GL46.GL_INT, new int[]{4, 4}),
    sampler2D("sampler2D", GL46.GL_SAMPLER_2D, new int[]{0, 0}),
    samplerCube("samplerCube", GL46.GL_SAMPLER_CUBE, new int[]{0, 0}),
    ivec2("ivec2", GL46.GL_INT_VEC2, new int[]{0, 0}),
    ivec3("ivec3", GL46.GL_INT_VEC3, new int[]{0, 0}),
    bool("bool", GL46.GL_BOOL, new int[]{4, 4});

    private final int glType;
    private final String glslName;
    private final int[] sizes;

    GLSLType(String glslName, int glType, int[] sizes) {
        this.glType = glType;
        this.glslName = glslName;
        this.sizes = sizes;
    }

    public int[] getSizes() {
        return sizes;
    }

    public int getGlType() {
        return glType;
    }

    @Nullable
    public static GLSLType valueOfEnum(String type) {
        for (var t : GLSLType.values()) {
            if (t.getGlslName().equals(type)) {
                return t;
            }
        }
        return null;
    }

    public String getGlslName() {
        return glslName;
    }
}