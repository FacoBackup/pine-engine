package com.pine.common;

import com.pine.common.messaging.Loggable;

public interface Renderable extends Loggable {
    void render();

    default void onInitialize() {
    }
}
