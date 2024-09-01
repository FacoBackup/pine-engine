package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGui;

public class InlineView extends AbstractView {

    public InlineView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void render() {
        int qtt = children.size() - 1;
        for (int i = 0; i <= qtt; i++) {
            View child = children.get(i);
            child.render();
            if (i < qtt) {
                ImGui.sameLine();
            }
        }
    }
}
