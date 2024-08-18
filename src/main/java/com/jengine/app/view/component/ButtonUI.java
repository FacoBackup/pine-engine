package com.jengine.app.view.component;

import com.jengine.app.view.core.state.ConstStringState;
import imgui.ImGui;

public abstract class ButtonUI extends AbstractUI<ConstStringState>{
    public ButtonUI(ConstStringState state) {
        super(state);
    }

    @Override
    public void render() {
        if (ImGui.button(state.getState())) {
            onClick();
        }
    }

    public abstract void onClick();
}
