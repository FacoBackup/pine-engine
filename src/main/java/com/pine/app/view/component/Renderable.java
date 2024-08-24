package com.pine.app.view.component;

import com.pine.app.Loggable;

public interface Renderable extends Loggable {
    void onInitialize();

    void render();
}
