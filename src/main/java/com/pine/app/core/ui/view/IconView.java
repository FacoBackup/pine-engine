package com.pine.app.core.ui.view;

import com.pine.app.core.Icon;
import com.pine.app.core.ui.View;
import imgui.ImGui;

public class IconView extends AbstractView {
    private Icon icon;

    public IconView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        icon = Icon.iconOfName(innerText);
    }

    @Override
    protected void renderInternal() {
        if (icon == null) {
            return;
        }
        ImGui.text(icon.getCodePoint());
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
