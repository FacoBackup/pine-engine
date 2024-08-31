package com.pine.app.view.core.component;


public interface View extends Renderable {

    String getInnerText();

    boolean isVisible();

    void setVisible(boolean visible);

    String getId();

    View getElementById(String id);

    View getParent();

    View getPanel();

    int[] getWindowDimensions();
}
