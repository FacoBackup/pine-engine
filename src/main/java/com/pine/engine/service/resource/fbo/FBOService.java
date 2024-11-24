package com.pine.engine.service.resource.fbo;

import com.pine.common.injection.PBean;
import com.pine.engine.service.resource.AbstractResourceService;

import java.util.HashMap;
import java.util.Map;

@PBean
public class FBOService extends AbstractResourceService<FBO, FBOCreationData> {
    public final Map<String, FBOCreationData> data = new HashMap<>();
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
        this.data.put(fbo.id, data);
        data.getSamplers().forEach(color -> {
            if (color.isDepth()) {
                fbo.depthTexture();
                color.setId(fbo.getDepthSampler());
            } else {
                fbo.addSampler(color.attachment(), color.precision(), color.format(), color.type(), color.linear(), color.repeat());
                color.setId(fbo.getSamplers().getLast());
            }
        });

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

    @Override
    public void dispose(FBO instance) {
        super.dispose(instance);
        this.data.remove(instance.id);
    }
}
