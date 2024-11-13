package com.pine.service.voxelization.util;

import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.voxelization.svo.VoxelColorData;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextureUtil.class);

    private static int[] uvToPixelCoords(float u, float v, int imgWidth, int imgHeight) {
        // Clamp UV coordinates to ensure they are within [0,1]
        u = Math.max(0, Math.min(u, 1));
        v = Math.max(0, Math.min(v, 1));

        // Correct horizontal flipping by flipping the U coordinate
        int x = (int) (u * (imgWidth - 1));
        int y = (int) (v * (imgHeight - 1));

        return new int[]{x, y};
    }

    public static VoxelColorData sampleTextureAtUV(TextureStreamData image, float u, float v) {
        if (image == null) {
            return new VoxelColorData(0, 0, 0);
        }

        int[] pixelCoords = uvToPixelCoords(u, v, image.width, image.height);
        int x = pixelCoords[0];
        int y = pixelCoords[1];

        int pixelIndex = (y * image.width + x) * 4; // 4 bytes per pixel (RGBA)

        int r = (image.imageBuffer.get(pixelIndex) & 0xFF);
        int g = (image.imageBuffer.get(pixelIndex + 1) & 0xFF);
        int b = (image.imageBuffer.get(pixelIndex + 2) & 0xFF);

        return new VoxelColorData(r, g, b);
    }

    public static TextureResourceRef create3DTexture(int width, int height, int depth, int internalFormat, int format, int type) {
        int textureID = GL46.glGenTextures();

        GL46.glBindTexture(GL46.GL_TEXTURE_3D, textureID);

        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);
        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_WRAP_R, GL46.GL_REPEAT);
        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);

        GL46.glTexImage3D(GL46.GL_TEXTURE_3D, 0, internalFormat, width, height, depth, 0, format, type, (ByteBuffer) null);

        var texture = new TextureResourceRef(null);
        texture.texture = textureID;
        texture.width = width;
        texture.height = height;
        texture.depth = depth;
        texture.internalFormat = internalFormat;

        GL46.glBindTexture(GL46.GL_TEXTURE_3D, GL11.GL_NONE);

        return texture;
    }

}
