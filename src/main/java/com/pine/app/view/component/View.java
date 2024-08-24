package com.pine.app.view.component;

import com.pine.app.Loggable;

public interface View extends Loggable {
    void onInitialize();

    void render(long index);

    boolean isVisible();

    default void setVisible(boolean visible) {
    }

    String getId();

    View getElementById(String id);

}
