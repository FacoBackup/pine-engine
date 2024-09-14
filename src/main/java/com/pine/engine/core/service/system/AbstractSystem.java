package com.pine.engine.core.service.system;


import com.pine.common.Initializable;
import com.pine.engine.core.service.resource.fbo.FBO;

public abstract class AbstractSystem implements Initializable {
    final public void render() {
        if (!isRenderable()) {
            return;
        }

        FBO fbo = getTargetFBO();
        if (fbo != null) {
            fbo.startMapping(shouldClearFBO());
            renderInternal();
            fbo.stop();
        } else {
            renderInternal();
        }
    }

    protected FBO getTargetFBO() {
        return null;
    }

    protected void renderInternal() {

    }

    protected boolean isRenderable() {
        return true;
    }

    protected boolean shouldClearFBO() {
        return true;
    }

    @Override
    public void onInitialize() {
    }
}
