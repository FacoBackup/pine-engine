package com.pine.service.environment;

import com.pine.service.streaming.impl.CubeMapFace;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class CubeMapWriteUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CubeMapWriteUtil.class);
    private static final int CHANNELS = 4;

    public static void saveCubeMapToDisk(int textureId, int imageSize, String basePath) {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, textureId);
        ByteBuffer buffer = BufferUtils.createByteBuffer(imageSize * imageSize * CHANNELS);
        for (int i = 0; i < CubeMapFace.SIZE; i++) {
            GL46.glGetTexImage(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, buffer);
            String path = getPathToFile(basePath, CubeMapFace.values()[i]);
            saveImage(path, imageSize, buffer);
        }
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
    }

    private static void saveImage(String filePath, int imageSize, ByteBuffer buffer) {
        buffer.flip();
        boolean result;

        MemoryStack stack = stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(filePath, true);
            long filenameEncoded = stack.getPointerAddress();
            result = STBImageWrite.nstbi_write_png(filenameEncoded, imageSize, imageSize, CHANNELS, memAddress(buffer), imageSize * CHANNELS) != 0;
        } finally {
            stack.setPointer(stackPointer);
        }

        LOGGER.warn("Writing result {} for {}", result, filePath);
        buffer.clear();
    }

    public static String getPathToFile(String basePath, CubeMapFace face) {
        return basePath + face.name();
    }
}
