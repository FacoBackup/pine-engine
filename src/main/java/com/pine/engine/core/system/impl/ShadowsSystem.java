package com.pine.engine.core.system.impl;

import com.pine.engine.core.system.AbstractSystem;

public class ShadowsSystem extends AbstractSystem {
    @Override
    public void render() {
        processOmnidirectional();
        processDirectional();
    }

    private void processDirectional() {
    }

    private void processOmnidirectional() {
    }
}