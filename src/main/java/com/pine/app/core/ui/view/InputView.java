package com.pine.app.core.ui.view;

import com.pine.app.core.state.StringState;
import com.pine.app.core.ui.View;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;

import java.util.function.Consumer;

public class InputView extends AbstractView {
    private Consumer<String> onChange;
    private boolean enabled = true;
    private final StringState state = new StringState(100);

    public InputView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
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
