package com.pine.service.resource.compute;

import org.lwjgl.opengl.GL46;

public class ComputeRuntimeData {
    public int memoryBarrier;
    public int groupX;
    public int groupY;
    public int groupZ;

    private ComputeRuntimeData(int memoryBarrier, int groupX, int groupY, int groupZ) {
        this.memoryBarrier = memoryBarrier;
        this.groupX = groupX;
        this.groupY = groupY;
        this.groupZ = groupZ;
    }

    public ComputeRuntimeData() {}

    public static ComputeRuntimeData ofLargeWorkGroup(Integer memoryBarrier) {
        return new ComputeRuntimeData(memoryBarrier == null ? GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT : memoryBarrier, 32, 32, 1);
    }

    public static ComputeRuntimeData ofNormalWorkGroup(Integer memoryBarrier) {
        return new ComputeRuntimeData(memoryBarrier == null ? GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT : memoryBarrier, 16, 16, 1);
    }
}
