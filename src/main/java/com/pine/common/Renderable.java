package com.pine.common;

public interface Renderable extends Loggable, Initializable {
    void tick();

    void render();
}
