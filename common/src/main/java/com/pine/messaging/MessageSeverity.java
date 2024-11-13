package com.pine.messaging;

import com.pine.theme.Icons;
import imgui.ImVec4;

public enum MessageSeverity {
    ERROR(new ImVec4(1f, 0, 0, .85f), Icons.error),
    SUCCESS(new ImVec4(0, 1f, 0, .85f), Icons.check_circle),
    WARN(new ImVec4(1f, 1f, 0, .85f), Icons.warning);

    private final ImVec4 color;
    private final String icon;

    MessageSeverity(ImVec4 color, String icon) {
        this.color = color;
        this.icon = icon;
    }

    public ImVec4 getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }
}
