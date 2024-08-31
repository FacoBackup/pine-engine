package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGui;

public class InlineView extends AbstractView {
    public InlineView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render() {
        for (View child : children) {
            child.render();
            ImGui.sameLine();
        }
    }
}
