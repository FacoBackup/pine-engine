package com.pine.app.view.component.view;

import com.pine.app.view.component.View;
import com.pine.app.view.component.panel.AbstractPanel;
import com.pine.app.view.core.state.StringState;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;

import java.util.function.Consumer;

public class InputView extends AbstractView {
    private Consumer<String> onChange;
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
                if (onChange != null) {
                    onChange.accept(state.toString());
                }
            }
        } else {
            ImGui.text(state.toString());
            ImGui.sameLine();
        }
    }

    public void setOnChange(Consumer<String> onChange) {
        this.onChange = onChange;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public StringState getState() {
        return state;
    }

    public void setState(String state) {
        this.state.setState(state);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
