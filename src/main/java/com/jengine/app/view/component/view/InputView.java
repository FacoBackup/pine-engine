package com.jengine.app.view.component.view;

import com.jengine.app.view.component.View;
import com.jengine.app.view.component.panel.AbstractPanel;
import com.jengine.app.view.core.state.StringState;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;

public abstract class InputView extends AbstractView{
    private boolean enabled = false;
    private final StringState state = new StringState(100);

    public InputView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render(long index) {
        if (enabled) {
            if (ImGui.inputText("##edit" + index, state.getState(), ImGuiInputTextFlags.EnterReturnsTrue)) {
                enabled = false;
                onChange();
            }
        } else {
            ImGui.text(state.toString());
            ImGui.sameLine();
        }
    }

    abstract void onChange();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
