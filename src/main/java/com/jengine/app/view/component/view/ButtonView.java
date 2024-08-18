package com.jengine.app.view.component.view;

import com.jengine.app.view.component.View;
import com.jengine.app.view.component.panel.AbstractPanel;
import imgui.ImGui;

public abstract class ButtonView extends AbstractView {

    public ButtonView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render(long index) {
        if(!visible){
            return;
        }

        if (ImGui.button(innerText + "##" + index)) {
            onClick();
        }
    }

    public abstract void onClick();
}
