package com.pine.core.service.resource;

import com.pine.Engine;
import com.pine.core.EngineDependency;
import com.pine.core.EngineInjectable;
import com.pine.core.service.resource.fbo.FBO;
import com.pine.core.service.resource.fbo.FBOCreationData;
import com.pine.core.service.resource.primitives.EmptyRuntimeData;
import com.pine.core.service.resource.resource.AbstractResourceService;
import com.pine.core.service.resource.resource.IResource;
import com.pine.core.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

@EngineInjectable
public class FBOService extends AbstractResourceService<FBO, EmptyRuntimeData, FBOCreationData> {
    private FBO current;

    @EngineDependency
    public Engine engine;

    @Override
    protected void unbind() {
        if (current != null) {
            current.stop();
        }
    }

    @Override
    protected void bindInternal(FBO instance, EmptyRuntimeData data) {
        current = instance;
        current.use();
    }

    @Override
    protected void bindInternal(FBO instance) {
        current = instance;
        current.use();
    }

    @Override
    protected IResource addInternal(FBOCreationData data) {
        int w = engine.displayW;
        int h = engine.displayH;
        if (data.getW() != null) {
            w = data.getW();
        }
        if (data.getH() != null) {
            h = data.getH();
        }
        var fbo = new FBO(w, h);
        data.getColors().forEach(color -> {
            if (color.w() == null || color.h() == null) {
                fbo.texture(color.attachment(), color.precision(), color.format(), color.type(), color.linear(), color.repeat());
            } else {
                fbo.texture(color.w(), color.h(), color.attachment(), color.precision(), color.format(), color.type(), color.linear(), color.repeat());
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
    protected void removeInternal(FBO resource) {
        resource.getColors().forEach(GL46::glDeleteTextures);
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
