package com.pine.service.resource.compute;

import org.lwjgl.opengl.GL46;

public class ComputeRuntimeData {
    public final int memoryBarrier;
    public final int groupX;
    public final int groupY;
    public final int groupZ;

    private ComputeRuntimeData(int memoryBarrier, int groupX, int groupY, int groupZ) {
        this.memoryBarrier = memoryBarrier;
        this.groupX = groupX;
        this.groupY = groupY;
        this.groupZ = groupZ;
    }

    public static ComputeRuntimeData ofLargeWorkGroup(Integer memoryBarrier) {
        return new ComputeRuntimeData(memoryBarrier == null ? GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT : memoryBarrier, 32, 32, 1);
    }

    public static ComputeRuntimeData ofNormalWorkGroup(Integer memoryBarrier) {
        return new ComputeRuntimeData(memoryBarrier == null ? GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT : memoryBarrier, 16, 16, 1);
    }
}
