package com.pine.service.environment;

import com.pine.service.streaming.impl.CubeMapFace;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class CubeMapGenerator {
    /**
     * Generates a framebuffer and a cube map texture for rendering.
     *
     * @return An array containing [framebufferId, cubeMapTextureId].
     */
    public static int[] generateFramebufferAndCubeMapTexture(int imageSize) {
        // Generate framebuffer
        int framebufferId = GL46.glGenFramebuffers();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, framebufferId);

        // Generate the cube map texture
        int cubeMapTextureId = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, cubeMapTextureId);

        // Allocate texture storage for each cube map face
        for (CubeMapFace face : CubeMapFace.values()) {
            GL46.glTexImage2D(face.getGlFace(), 0, GL46.GL_RGBA16F, imageSize, imageSize, 0, GL46.GL_RGBA, GL46.GL_FLOAT, (ByteBuffer) null);
        }

        // Set texture parameters
        setUpCubeMapTexture();

        int depthRenderBufferId = GL46.glGenRenderbuffers();
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, depthRenderBufferId);
        GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH_COMPONENT24, imageSize, imageSize);
        GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, GL46.GL_RENDERBUFFER, depthRenderBufferId);

        // Unbind the framebuffer and the cube map texture
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, GL11.GL_NONE);
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, GL11.GL_NONE);

        return new int[]{framebufferId, cubeMapTextureId};
    }

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
