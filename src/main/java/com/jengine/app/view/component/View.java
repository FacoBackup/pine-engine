package com.jengine.app.view.component;

public interface View {
    void onInitialize();

    void render(long index);

    boolean isVisible();

    default void setVisible(boolean visible) {
    }

    String getId();

    View getElementById(String id);

}
