package com.pine.app.view.core.component.view;

import com.pine.app.view.core.component.View;
import com.pine.app.view.core.component.panel.AbstractPanel;
import com.pine.app.view.core.state.StringState;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;

import java.util.function.Consumer;

public class InputView extends AbstractView {
    private Consumer<String> onChange;
    private boolean enabled = true;
    private final StringState state = new StringState(100);

    public InputView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render() {
        int flags;
        if (!enabled) {
            flags = ImGuiInputTextFlags.ReadOnly;
        } else {
            flags = ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.CallbackEdit;
        }

        if (ImGui.inputText(internalId, state.getState(), flags)) {
            enabled = false;
            if (onChange != null) {
                onChange.accept(state.get());
            }
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

    public String getValue() {
        return state.get();
    }
}
