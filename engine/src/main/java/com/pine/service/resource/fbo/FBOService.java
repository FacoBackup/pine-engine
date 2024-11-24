package com.pine.service.resource.fbo;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.resource.AbstractResourceService;

@PBean
public class FBOService extends AbstractResourceService<FBO, FBOCreationData> {
    private FBO current;

    @Override
    protected FBO createInternal(FBOCreationData data) {
        int w = runtimeRepository.getDisplayW();
        int h = runtimeRepository.getDisplayH();
        if (data.getW() != null) {
            w = data.getW();
        }
        if (data.getH() != null) {
            h = data.getH();
        }
        var fbo = new FBO(w, h);
        data.getSamplers().forEach(color -> {
            fbo.addSampler(color.attachment(), color.precision(), color.format(), color.type(), color.linear(), color.repeat());
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
    protected void unbind() {
        if (current != null) {
            current.stop();
        }
    }

    @Override
    public void bind(FBO instance) {
        if (current == instance) {
            return;
        }
        current = instance;
        current.use();
    }
}
