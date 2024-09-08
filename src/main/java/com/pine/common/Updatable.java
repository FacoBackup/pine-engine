package com.pine.common;

public interface Updatable {
    void tick();

    default void onInitialize() {
    }
}
