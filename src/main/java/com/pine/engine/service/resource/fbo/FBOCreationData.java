package com.pine.engine.service.resource.fbo;

import com.pine.engine.service.resource.IResourceCreationData;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

public class FBOCreationData implements IResourceCreationData {
    private final List<FBOTextureData> colors = new ArrayList<>();
    private boolean depthTexture;
    private boolean depthTest;
    private final Integer w;
    private final Integer h;

    public FBOCreationData(int w, int h, boolean depthTexture, boolean depthTest) {
        this.depthTexture = depthTexture;
        this.depthTest = depthTest;
        this.w = w;
        this.h = h;
    }

    public Integer getW() {
        return w;
    }

    public Integer getH() {
        return h;
    }

    public List<FBOTextureData> getSamplers() {
        return colors;
    }

    public boolean isDepthTexture() {
        return depthTexture;
    }

    public FBOCreationData addSampler(int attachment, int precision, int format, int type, boolean linear, boolean repeat) {
        colors.add(new FBOTextureData(attachment, precision, format, type, linear, repeat));
        return this;
    }

    public FBOCreationData addSampler() {
        addSampler(0, GL46.GL_RGBA8, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false);
        return this;
    }

    public boolean isDepthTest() {
        return depthTest;
    }

    public FBOCreationData setDepthTest(boolean depthTest) {
        this.depthTest = depthTest;
        return this;
    }

    public FBOCreationData setDepthTexture(boolean depthTexture) {
        this.depthTexture = depthTexture;
        return this;
    }
}
