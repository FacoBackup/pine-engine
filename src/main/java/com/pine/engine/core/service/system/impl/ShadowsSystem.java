package com.pine.engine.core.service.system.impl;

import com.pine.engine.core.service.system.AbstractSystem;

public class ShadowsSystem extends AbstractSystem {
    @Override
    protected void renderInternal() {
        processOmnidirectional();
        processDirectional();
    }

    private void processDirectional() {
    }

    private void processOmnidirectional() {
    }
}
