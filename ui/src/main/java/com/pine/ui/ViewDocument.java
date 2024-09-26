package com.pine.ui;

import com.pine.AbstractWindow;
import com.pine.Loggable;
import com.pine.PInjector;
import com.pine.ui.view.AbstractView;
import imgui.ImGui;
import imgui.ImVec2;

public class ViewDocument implements Loggable {
    private final AbstractWindow window;
    private final PInjector injector;

    public ViewDocument(AbstractWindow window, PInjector injector) {
        this.window = window;
        this.injector = injector;
    }

    public AbstractWindow getWindow() {
        return window;
    }

    public ImVec2 getViewportDimensions() {
        return ImGui.getMainViewport().getSize();
    }

    final public void appendChild(View child, View parent) {
        injector.inject(child);
        parent.getChildren().add(child);

        if (child.getContext() == null || parent.getContext() != null) {
            child.setContext(parent.getContext());
        }
        child.setParent(parent);
        child.setDocument(this);
        child.onInitialize();
    }

    public void removeChild(View child, AbstractView parent) {
        parent.getChildren().remove(child);
    }
}
