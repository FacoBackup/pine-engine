package com.pine.common;

public interface EngineComponent extends Initializable {
    void tick();

    @Override
    default void onInitialize() {
    }
}
