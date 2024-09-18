package com.pine.ui.view;

import com.pine.ui.View;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.util.function.Consumer;

public class InputView extends AbstractView {
    private Consumer<String> onChange;
    private boolean enabled = true;
    private final ImString value = new ImString(100);

    public InputView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        int flags;
        if (!enabled) {
            flags = ImGuiInputTextFlags.ReadOnly;
        } else {
            flags = ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.CallbackEdit;
        }

        if (ImGui.inputText(internalId, value, flags)) {
            enabled = false;
            if (onChange != null) {
                onChange.accept(value.get());
            }
        }
    }

    public void setOnChange(Consumer<String> onChange) {
        this.onChange = onChange;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setValue(String state) {
        this.value.set(state);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getValue() {
        return value.get();
    }
}
