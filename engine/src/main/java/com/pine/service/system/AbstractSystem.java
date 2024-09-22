package com.pine.service.system;


import com.pine.Initializable;
import com.pine.service.resource.fbo.FrameBufferObject;

public abstract class AbstractSystem implements Initializable {
    final public void render() {
        if (!isRenderable()) {
            return;
        }

        FrameBufferObject fbo = getTargetFBO();
        if (fbo != null) {
            fbo.startMapping(shouldClearFBO());
            renderInternal();
            fbo.stop();
        } else {
            renderInternal();
        }
    }

    protected FrameBufferObject getTargetFBO() {
        return null;
    }

    protected void renderInternal() {

    }

    protected boolean isRenderable() {
        return true;
    }

    protected boolean shouldClearFBO() {
        return false;
    }

    @Override
    public void onInitialize() {
    }
}
