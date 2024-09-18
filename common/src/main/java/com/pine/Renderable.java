package com.pine;

public interface Renderable extends Loggable, Initializable {
    void tick();

    void render();

    @Override
    default void onInitialize() {
    }
}
