package com.pine.service.resource.fbo;

import com.pine.EngineUtils;
import com.pine.service.resource.AbstractResource;
import com.pine.service.resource.LocalResourceType;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class FrameBufferObject extends AbstractResource {
    public final int width;
    public final int height;
    private final int FBO;
    private int clearFlag = GL46.GL_COLOR_BUFFER_BIT;
    private Integer RBO = null;
    private Integer depthSampler = null;
    private final List<Integer> samplers = new ArrayList<>();
    private final List<Integer> attachments = new ArrayList<>();
    private final float[] resolution = new float[2];
    private int mainSampler;
    private int mainSamplerPrecision;

    public FrameBufferObject(int width, int height, String id) {
        super(id);
        this.width = width;
        this.height = height;
        this.resolution[0] = width;
        this.resolution[1] = height;
        this.FBO = GL46.glGenFramebuffers();
    }

    public int getFBO() {
        return FBO;
    }

    public void startMapping(boolean clearing) {
        use();
        GL46.glViewport(0, 0, this.width, this.height);
        if (clearing) {
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        }
    }

    public Integer getDepthSampler() {
        return depthSampler;
    }

    public Integer getRBO() {
        return RBO;
    }

    public float[] getResolution() {
        return resolution;
    }

    public List<Integer> getSamplers() {
        return samplers;
    }

    public void depthTexture() {
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
    }

    public void depthTest() {
        use();
        this.RBO = GL46.glGenRenderbuffers();
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, this.RBO);
        GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH_COMPONENT24, this.width, this.height);
        GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, GL46.GL_RENDERBUFFER, this.RBO);
        clearFlag = GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT;

    }

    public FrameBufferObject sampler() {
        sampler(this.width, this.height, 0, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false);
        return this;
    }

    public void sampler(int attachment, int precision, int format, int type, boolean linear, boolean repeat) {
        sampler(this.width, this.height, attachment, precision, format, type, linear, repeat);
    }

    public void sampler(int w, int h, int attachment, int precision, int format, int type, boolean linear, boolean repeat) {
        use();
        int texture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, repeat ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, repeat ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);

        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, precision, w, h, 0, format, type, (FloatBuffer) null);
        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0 + attachment, GL46.GL_TEXTURE_2D, texture, 0);

        samplers.add(texture);
        if (samplers.size() == 1) {
            mainSampler = texture;
            mainSamplerPrecision = precision;
        }
        attachments.add(GL46.GL_COLOR_ATTACHMENT0 + attachment);
        GL46.glDrawBuffers(attachments.stream().mapToInt(i -> i).toArray());

    }

    public void use() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, this.FBO);
    }

    public void clear() {
        use();
        GL46.glClear(clearFlag);
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, GL46.GL_NONE);
    }

    public void stop() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, GL46.GL_NONE);
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.FBO;
    }

    public int getMainSampler() {
        return mainSampler;
    }

    @Override
    public void dispose() {
        for (var sampler : samplers) {
            GL46.glDeleteTextures(sampler);
        }
        if (depthSampler != null) {
            GL46.glDeleteTextures(depthSampler);
        }
        GL46.glDeleteFramebuffers(FBO);
        if (RBO != null) {
            GL46.glDeleteFramebuffers(RBO);
        }
    }

    public void bindForCompute() {
        GL46.glBindImageTexture(0, mainSampler, 0, false, 0, GL46.GL_WRITE_ONLY, mainSamplerPrecision);
    }
}
