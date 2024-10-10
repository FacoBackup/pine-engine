package com.pine;

import com.pine.messaging.Loggable;

public interface Renderable extends Loggable {
    default void tick() {
    }

    void render();

    default void onInitialize() {
    }
}
