package com.pine.engine.core.service.system.impl;

import com.pine.engine.core.service.system.AbstractSystem;

public class PostProcessingSystem extends AbstractSystem {

    @Override
    protected void renderInternal() {
        doBokehDOF();
        doMotionBlur();
        doBloom();
    }

    private void doBokehDOF() {
    }

    private void doMotionBlur() {
    }

    private void doBloom() {
    }
}
