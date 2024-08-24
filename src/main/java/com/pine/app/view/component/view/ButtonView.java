package com.pine.app.view.component.view;

import com.pine.app.view.component.View;
import com.pine.app.view.component.panel.AbstractPanel;
import imgui.ImGui;

public class ButtonView extends AbstractView {
    private Runnable onClick;

    public ButtonView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render() {
        if (!visible) {
            return;
        }

        if (ImGui.button(innerText + internalId) && onClick != null) {
            onClick.run();
        }
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}
