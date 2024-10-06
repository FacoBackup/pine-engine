package com.pine.messaging;

import imgui.ImVec4;

public enum MessageSeverity {
    ERROR(new ImVec4(1f, 0, 0, .5f)),
    SUCCESS(new ImVec4(0, 1f, 0, .5f)),
    WARN(new ImVec4(1f, 1f, 0, .5f));

    private final ImVec4 color;

    MessageSeverity(ImVec4 color) {
        this.color = color;
    }

    public ImVec4 getColor() {
        return color;
    }
}
