package com.pine.core.resource;

import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL46;

public enum GLSLType {
    vec2("vec2", GL46.GL_FLOAT_VEC2),
    vec3("vec3", GL46.GL_FLOAT_VEC3),
    vec4("vec4", GL46.GL_FLOAT_VEC4),
    mat3("mat3", GL46.GL_FLOAT_MAT3),
    mat4("mat4", GL46.GL_FLOAT_MAT4),
    f("float", GL46.GL_FLOAT),
    i("int", GL46.GL_INT),
    sampler2D("sampler2D", GL46.GL_SAMPLER_2D),
    samplerCube("samplerCube", GL46.GL_SAMPLER_CUBE),
    ivec2("ivec2", GL46.GL_INT_VEC2),
    ivec3("ivec3", GL46.GL_INT_VEC3),
    bool("bool", GL46.GL_BOOL);

    private final int glType;
    private final String glslName;

    GLSLType(String glslName, int glType) {
        this.glType = glType;
        this.glslName = glslName;
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