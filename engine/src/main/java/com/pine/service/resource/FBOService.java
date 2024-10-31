package com.pine.service.resource;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.resource.fbo.FrameBufferObject;

@PBean
public class FBOService extends AbstractResourceService<FrameBufferObject> {
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
    public void bind(FrameBufferObject instance) {
        if (current == instance) {
            return;
        }
        current = instance;
        current.use();
    }

}
