package com.pine;

public interface Renderable extends Loggable, Initializable {
    default void tick() {
    }

    void render();

    @Override
    default void onInitialize() {
    }
}
