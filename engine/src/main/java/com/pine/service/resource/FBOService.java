package com.pine.service.resource;

import com.pine.Engine;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.primitives.EmptyRuntimeData;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

@PBean
public class FBOService extends AbstractResourceService<FrameBufferObject, EmptyRuntimeData, FBOCreationData> {
    private FrameBufferObject current;

    @PInject
    public Engine engine;

    @Override
    protected void unbind() {
        if (current != null) {
            current.stop();
        }
    }

    @Override
    protected void bindInternal(FrameBufferObject instance, EmptyRuntimeData data) {
        if (current == instance) {
            return;
        }
        current = instance;
        current.use();
    }

    @Override
    protected void bindInternal(FrameBufferObject instance) {
        current = instance;
        current.use();
    }

    @Override
    protected IResource addInternal(FBOCreationData data) {
        int w = engine.getDisplayW();
        int h = engine.getDisplayH();
        if (data.getW() != null) {
            w = data.getW();
        }
        if (data.getH() != null) {
            h = data.getH();
        }
        var fbo = new FrameBufferObject(w, h);
        data.getSamplers().forEach(color -> {
            if (color.w() == null || color.h() == null) {
                fbo.sampler(color.attachment(), color.precision(), color.format(), color.type(), color.linear(), color.repeat());
            } else {
                fbo.sampler(color.w(), color.h(), color.attachment(), color.precision(), color.format(), color.type(), color.linear(), color.repeat());
            }
        });

        if (data.isDepthTexture()) {
            fbo.depthTexture();
        }

        if (data.isDepthTest()) {
            fbo.depthTest();
        }
        return fbo;
    }

    @Override
    protected void removeInternal(FrameBufferObject resource) {
        resource.getSamplers().forEach(GL46::glDeleteTextures);
        if (resource.getDepthSampler() != null) {
            GL46.glDeleteTextures(resource.getDepthSampler());
        }
        if (resource.getRBO() != null) {
            GL46.glDeleteRenderbuffers(resource.getRBO());
        }
        GL46.glDeleteFramebuffers(resource.getFBO());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.FBO;
    }
}
