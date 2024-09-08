package com.pine.engine;

import org.lwjgl.opengl.GL46;

public class EngineUtils {
    public static void bindTexture2d(int location, int activeIndex, int sampler) {
        GL46.glActiveTexture(GL46.GL_TEXTURE0 + activeIndex);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, sampler);
        GL46.glUniform1i(location, activeIndex);
    }
}
