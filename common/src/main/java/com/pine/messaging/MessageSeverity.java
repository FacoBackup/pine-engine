package com.pine.messaging;

import imgui.ImVec4;

public enum MessageSeverity {
    ERROR(new ImVec4(1f, 0, 0, .85f)),
    SUCCESS(new ImVec4(0, 1f, 0, .85f)),
    WARN(new ImVec4(1f, 1f, 0, .85f));

    private final ImVec4 color;

    MessageSeverity(ImVec4 color) {
        this.color = color;
    }

    public ImVec4 getColor() {
        return color;
    }
}
