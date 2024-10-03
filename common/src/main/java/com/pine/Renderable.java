package com.pine;

public interface Renderable extends Loggable {
    default void tick() {
    }

    void render();

    default void onInitialize() {
    }
}
