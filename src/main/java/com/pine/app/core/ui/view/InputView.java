package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.util.function.Consumer;

public class InputView extends AbstractView {
    private Consumer<String> onChange;
    private boolean enabled = true;
    private final ImString state = new ImString(100);

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

        if (ImGui.inputText(internalId, state, flags)) {
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

    public void setState(String state) {
        this.state.set(state);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getValue() {
        return state.get();
    }
}
