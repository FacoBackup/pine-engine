package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGui;

public class ButtonView extends AbstractView {
    private Runnable onClick;

    public ButtonView(View parent, String id) {
        super(parent, id);
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
