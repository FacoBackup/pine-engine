package com.pine.app.core.ui;

import com.pine.common.Loggable;

public interface Renderable extends Loggable {
    void onInitialize();

    void render();
}
