package com.pine.tools.system;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.component.CullingComponent;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.system.AbstractSystem;

public class CullingVisualizationSystem extends AbstractSystem {
    @PInject
    public Engine engine;

    @PInject
    public CullingComponent cullingComponents;

    @Override
    protected FBO getTargetFBO() {
        return engine.getTargetFBO();
    }

    @Override
    protected void renderInternal() {
        // TODO - RENDER CULLING BOXES
    }
}
