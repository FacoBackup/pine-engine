package com.pine.service.environment;

import com.pine.service.streaming.LevelOfDetail;
import com.pine.service.streaming.impl.CubeMapFace;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImageWrite;

import java.nio.ByteBuffer;

public class CubeMapWriteUtil {
    public static void saveCubeMapToDisk(int textureId, int imageSize, String basePath, LevelOfDetail lod) {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, textureId);
        ByteBuffer buffer = BufferUtils.createByteBuffer(imageSize * imageSize * 4);
        for (int i = 0; i < CubeMapFace.values().length; i++) {
            GL46.glGetTexImage(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, buffer);
            String path = getPathToFile(basePath, lod, CubeMapFace.values()[i]);
            saveImage(path, imageSize, buffer);
        }
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
    }

    private static void saveImage(String filePath, int imageSize, ByteBuffer buffer) {
        buffer.flip();
        STBImageWrite.stbi_write_png(filePath, imageSize, imageSize, 4, buffer, imageSize * 4);
        buffer.clear();
    }

    public static String getPathToFile(String basePath, LevelOfDetail lod, CubeMapFace face) {
        return basePath + lod.name() + face.name();
    }
}
