package com.pine.core;

import com.pine.core.dock.DockPanel;
import com.pine.core.view.AbstractView;
import imgui.ImVec4;

public abstract class AbstractWindow extends AbstractView {
    protected final DockPanel root = new DockPanel();

    public void onInitialize() {
        appendChild(root);
        root.setHeader(getHeader());
        root.onInitialize();
    }

    public abstract AbstractView getHeader();

    public abstract ImVec4 getNeutralPalette();

    public abstract ImVec4 getAccentColor();

    public float getWindowScaleX() {
        return 1;
    }

    public float getWindowScaleY() {
        return 1;
    }

    @Override
    final public String getTitle() {
        return "Total frame cost";
    }
}
