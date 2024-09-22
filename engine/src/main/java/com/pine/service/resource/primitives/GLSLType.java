package com.pine.service.resource.primitives;

import org.lwjgl.opengl.GL46;

import javax.annotation.Nullable;

public enum GLSLType {
    VEC_2("vec2", GL46.GL_FLOAT_VEC2, new int[]{8, 8}, 8, 8),
    VEC_3("vec3", GL46.GL_FLOAT_VEC3, new int[]{16, 12}, 16, 16),
    VEC_4("vec4", GL46.GL_FLOAT_VEC4, new int[]{16, 16}, 16, 16),
    MAT_3("mat3", GL46.GL_FLOAT_MAT3, new int[]{48, 48}, 16, 48),
    MAT_4("mat4", GL46.GL_FLOAT_MAT4, new int[]{64, 64}, 16, 64),
    FLOAT("float", GL46.GL_FLOAT, new int[]{4, 4}, 4, 4),
    INT("int", GL46.GL_INT, new int[]{4, 4}, 4, 4),
    SAMPLER_2_D("sampler2D", GL46.GL_SAMPLER_2D, new int[]{0, 0}, 0, 0),
    SAMPLER_CUBE("samplerCube", GL46.GL_SAMPLER_CUBE, new int[]{0, 0}, 0, 0),
    IVEC_2("ivec2", GL46.GL_INT_VEC2, new int[]{0, 0}, 8, 8),
    IVEC_3("ivec3", GL46.GL_INT_VEC3, new int[]{0, 0}, 4, 4),
    BOOL("bool", GL46.GL_BOOL, new int[]{4, 4}, 4, 4);

    private final int glType;
    private final String glslName;
    private final int[] sizes;
    private final int size;
    private final int alignment;

    GLSLType(String glslName, int glType, int[] sizes, int alignment, int size) {
        this.glType = glType;
        this.glslName = glslName;
        this.sizes = sizes;
        this.size = size;
        this.alignment = alignment;
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

    /**
     * std430
     *
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * std430
     *
     * @return
     */
    public int getAlignment() {
        return alignment;
    }
}