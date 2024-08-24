package com.pine.app.view.component.view;

import com.pine.app.view.component.View;
import com.pine.app.view.component.panel.AbstractPanel;
import imgui.ImGui;

public class GroupView extends AbstractView {
    public GroupView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render() {
        ImGui.beginGroup();
        super.render();
        ImGui.endGroup();
    }
}
