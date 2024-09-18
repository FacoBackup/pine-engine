package com.pine.service.resource.fbo;

import com.pine.service.resource.resource.ResourceCreationData;
import com.pine.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

public class FBOCreationData implements ResourceCreationData {

    private final List<FBOTextureData> colors = new ArrayList<>();
    private boolean depthTexture;
    private boolean depthTest;
    private final Integer w;
    private final Integer h;

    public FBOCreationData(boolean depthTexture, boolean depthTest) {
        this.depthTexture = depthTexture;
        this.depthTest = depthTest;
        this.w = null;
        this.h = null;
    }

    public FBOCreationData(int w, int h) {
        this.w = w;
        this.h = h;
        this.depthTexture = false;
        this.depthTest = false;
    }

    public Integer getW() {
        return w;
    }

    public Integer getH() {
        return h;
    }

    public List<FBOTextureData> getColors() {
        return colors;
    }

    public boolean isDepthTexture() {
        return depthTexture;
    }

    public FBOCreationData addColor(int w, int h, int attachment, int precision, int format, int type, boolean linear, boolean repeat) {
        colors.add(new FBOTextureData(w, h, attachment, precision, format, type, linear, repeat));
        return this;
    }

    public FBOCreationData addColor(int attachment, int precision, int format, int type, boolean linear, boolean repeat) {
        colors.add(new FBOTextureData(null, null, attachment, precision, format, type, linear, repeat));
        return this;
    }

    public FBOCreationData addColor() {
        addColor(0, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false);
        return this;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.FBO;
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
