package com.pine.engine.core.service.system.impl;

import com.pine.engine.core.service.system.AbstractSystem;

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
