package com.pine.engine.core.gl;

import com.pine.engine.core.EngineUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class FBO {
    public final int width;
    public final int height;
    private final int FBO;
    private int RBO;
    private int depthSampler;
    private final List<Integer> colors = new ArrayList<>();
    private final List<Integer> attachments = new ArrayList<>();
    private final float[] resolution = new float[2];

    public FBO(int width, int height) {
        this.width = width;
        this.height = height;
        this.resolution[0] = width;
        this.resolution[1] = height;
        this.FBO = GL46.glGenFramebuffers();
    }

    public void startMapping(boolean noClearing) {
        use();
        GL46.glViewport(0, 0, this.width, this.height);
        if (!noClearing) {
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        }
    }

    public int getDepthSampler() {
        return depthSampler;
    }

    public int getRBO() {
        return RBO;
    }

    public void stopMapping() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    public float[] getResolution() {
        return resolution;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public FBO depthTexture() {
        use();
        this.depthSampler = EngineUtils.createTexture(
                this.width,
                this.height,
                GL46.GL_DEPTH_COMPONENT24,
                0,
                GL46.GL_DEPTH_COMPONENT,
                GL46.GL_UNSIGNED_INT,
                null,
                GL46.GL_NEAREST,
                GL46.GL_NEAREST,
                GL46.GL_CLAMP_TO_EDGE,
                GL46.GL_CLAMP_TO_EDGE,
                false
        );

        GL46.glFramebufferTexture2D(
                GL46.GL_FRAMEBUFFER,
                GL46.GL_DEPTH_ATTACHMENT,
                GL46.GL_TEXTURE_2D,
                this.depthSampler,
                0
        );
        return this;
    }

    public FBO depthTest() {
        use();
        this.RBO = GL46.glGenRenderbuffers();
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, this.RBO);
        GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH_COMPONENT24, this.width, this.height);
        GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, GL46.GL_RENDERBUFFER, this.RBO);

        return this;
    }

    public FBO texture() {
        texture(this.width, this.height, 0, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false);
        return this;
    }

    public FBO texture(int attachment, int precision, int format, int type, boolean linear, boolean repeat) {
        texture(this.width, this.height, attachment, precision, format, type, linear, repeat);
        return this;
    }

    public FBO texture(int w, int h, int attachment, int precision, int format, int type, boolean linear, boolean repeat) {
        use();
        int texture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, repeat ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, repeat ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);

        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, precision, w, h, 0, format, type, (FloatBuffer) null);
        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0 + attachment, GL46.GL_TEXTURE_2D, texture, 0);

        colors.add(texture);
        attachments.add(GL46.GL_COLOR_ATTACHMENT0 + attachment);
        GL46.glDrawBuffers(attachments.stream().mapToInt(i -> i).toArray());

        return this;
    }

    public void use() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, this.FBO);
    }

    public void clear() {
        use();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    public void stop() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }
}