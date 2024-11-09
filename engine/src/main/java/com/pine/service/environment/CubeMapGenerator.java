package com.pine.service.environment;

import com.pine.service.streaming.impl.CubeMapFace;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class CubeMapGenerator {
    public static void setUpCubeMapTexture() {
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_R, GL46.GL_CLAMP_TO_EDGE);
    }

    public static int generateTexture(int res){
        int preFilteredMap = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, preFilteredMap);
        for (int i = 0; i < CubeMapFace.values().length; ++i) {
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL46.GL_RGB16F, res, res, 0, GL46.GL_RGB, GL46.GL_FLOAT, (ByteBuffer) null);
        }

        setUpCubeMapTexture();

        return preFilteredMap;
    }
}
