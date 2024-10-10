package com.pine.tools.system;

import com.pine.component.MeshComponent;
import com.pine.injection.PInject;
import com.pine.service.system.AbstractSystem;

public class CullingVisualizationSystem extends AbstractSystem {
    @PInject
    public MeshComponent meshes;

//    @Override
//    protected FBO getTargetFBO() {
//        return engine.getTargetFBO();
//    }

    @Override
    protected void renderInternal() {
        // TODO - RENDER CULLING BOXES
    }
}
