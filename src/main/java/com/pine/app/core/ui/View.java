package com.pine.app.core.ui;


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
