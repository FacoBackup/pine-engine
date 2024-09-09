package com.pine.engine.core.system.impl;

import com.pine.engine.core.system.AbstractSystem;

public class PostProcessingSystem extends AbstractSystem {

    @Override
    public void render() {
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
