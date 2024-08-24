package com.pine.app.view.component;

import com.pine.app.Loggable;

public interface View extends Renderable {

    String getInnerText();

    boolean isVisible();

    default void setVisible(boolean visible) {

    }

    String getId();

    View getElementById(String id);

    View getParent();
}
