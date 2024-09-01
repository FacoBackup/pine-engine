package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;

public class InlineView extends AbstractView {

    public InlineView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        int qtt = children.size() - 1;
        for (int i = 0; i <= qtt; i++) {
            View child = children.get(i);
            child.render();
            if (i < qtt && child.isVisible()) {
                ImGui.sameLine();
            }
        }
    }
}
