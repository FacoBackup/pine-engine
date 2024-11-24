package com.pine.engine.service.resource.shader;

public enum GLSLType {
    VEC_2(new int[]{8, 8}, 8),
    VEC_3(new int[]{16, 12}, 16),
    VEC_4(new int[]{16, 16}, 16),
    MAT_3(new int[]{48, 48}, 48),
    MAT_4(new int[]{64, 64}, 64),
    FLOAT(new int[]{4, 4}, 4),
    INT(new int[]{4, 4}, 4),
    SAMPLER_2_D(new int[]{0, 0}, 0),
    SAMPLER_CUBE(new int[]{0, 0}, 0),
    IVEC_2(new int[]{0, 0}, 8),
    IVEC_3(new int[]{0, 0}, 4),
    BOOL(new int[]{4, 4}, 4);

    private final int[] sizes;
    private final int size;

    GLSLType(int[] sizes, int size) {
        this.sizes = sizes;
        this.size = size;
    }

    public int[] getSizes() {
        return sizes;
    }

    /**
     * std430
     *
     * @return
     */
    public int getSize() {
        return size;
    }

}