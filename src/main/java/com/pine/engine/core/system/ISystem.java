package com.pine.engine.core.system;

import com.pine.common.Initializable;
import com.pine.common.Renderable;

public interface ISystem extends Initializable, Renderable {
    /**
     * Logic and state update
     */
    default void tick() {
    }

    @Override
    default void render() {
    }

    @Override
    default void onInitialize() {
    }
}
