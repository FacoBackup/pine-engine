package com.pine.core;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class UIUtil {
    public static final int FIXED_WINDOW_FLAGS = ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse;
    public static final ImBoolean OPEN = new ImBoolean(true);
    private static final ImVec2 MEDIUM_SPACING = new ImVec2(5, 0);
    private static final ImVec2 LARGE_SPACING = new ImVec2(40, 0);

    public static boolean renderOption(String label, boolean selected, boolean fixedSize, ImVec4 accent) {
        float size = fixedSize ? ONLY_ICON_BUTTON_SIZE : -1;
        return renderOption(label, selected, size, size, accent);
    }

    public static boolean renderOption(String label, boolean selected, float sizeX, float sizeY, ImVec4 accent) {
        int popStyle = 0;
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.Button, accent);
            popStyle++;
        }
        boolean value;
        if (sizeX == -1) {
            value = ImGui.button(label);
        } else {
            value = ImGui.button(label, sizeX, sizeY);
        }

        ImGui.popStyleColor(popStyle);
        return value;
    }

    public static void largeSpacing() {
        ImGui.sameLine();
        ImGui.dummy(LARGE_SPACING);
        ImGui.sameLine();
    }

    public static void spacing() {
        ImGui.sameLine();
        ImGui.dummy(MEDIUM_SPACING);
        ImGui.sameLine();
    }

    public static void dynamicSpacing(float size) {
        ImGui.sameLine();
        ImGui.dummy(ImGui.getContentRegionAvailX() - size, 0);
        ImGui.sameLine();
    }
}
