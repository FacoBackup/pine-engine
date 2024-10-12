package com.pine;

import com.pine.messaging.Loggable;

public interface Renderable extends Loggable {
    void render();

    default void onInitialize() {
    }
}
