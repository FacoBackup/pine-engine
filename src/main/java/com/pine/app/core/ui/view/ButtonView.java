package com.pine.app.core.ui.view;

import com.pine.app.core.ui.MaterialIcon;
import com.pine.app.core.ui.View;
import imgui.ImGui;

public class ButtonView extends AbstractView {
    private Runnable onClick;

    public ButtonView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        if (onClick != null && ImGui.button(innerText + internalId)) {
            onClick.run();
        }
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void initializeIcons() {
        for (var icon : MaterialIcon.values()) {
            innerText = innerText.replace("[" + icon.getIconName() + "]", icon.getCodePoint());
        }
    }
}
