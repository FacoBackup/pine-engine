package com.pine.app.core.ui.theme;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;

public class ThemeUtil {
    public static void setTheme(boolean isDarkMode, float[] backgroundColor) {
        ImGuiStyle style = ImGui.getStyle();
        ImVec4[] colors = style.getColors();

        if (!isDarkMode) {
            setLightMode(backgroundColor, colors);
        } else {
            setDarkMode(backgroundColor, colors);
        }

        ImGui.getStyle().setColors(colors);
    }

    private static void setDarkMode(float[] backgroundColor, ImVec4[] colors) {
        var palette0 = new ImVec4(10.0f / 255.0f, 10.0f / 255.0f, 10.0f / 255.0f, 1.0f);
        var palette1 = new ImVec4(18.0f / 255.0f, 18.0f / 255.0f, 18.0f / 255.0f, 1.0f);
        var palette2 = new ImVec4(22.0f / 255.0f, 22.0f / 255.0f, 22.0f / 255.0f, 1.0f);
        var palette3 = new ImVec4(35.0f / 255.0f, 35.0f / 255.0f, 35.0f / 255.0f, 1.0f);
        var palette4 = new ImVec4(65.0f / 255.0f, 65.0f / 255.0f, 65.0f / 255.0f, 1.0f);
        var palette5 = new ImVec4(119.0f / 255.0f, 119.0f / 255.0f, 119.0f / 255.0f, 1.0f);
        var palette6 = new ImVec4(224.0f / 255.0f, 224.0f / 255.0f, 224.0f / 255.0f, 1.0f);

        backgroundColor[0] = palette2.x;
        backgroundColor[1] = palette2.y;
        backgroundColor[2] = palette2.z;

        colors[ImGuiCol.Text] = palette6;
        colors[ImGuiCol.TextDisabled] = palette6;
        colors[ImGuiCol.WindowBg] = palette1;
        colors[ImGuiCol.ChildBg] = palette1;
        colors[ImGuiCol.PopupBg] = palette1;
        colors[ImGuiCol.Border] = palette3;
        colors[ImGuiCol.BorderShadow] = palette0;
        colors[ImGuiCol.FrameBg] = palette2;
        colors[ImGuiCol.FrameBgHovered] = palette3;
        colors[ImGuiCol.FrameBgActive] = palette4;
        colors[ImGuiCol.TitleBg] = palette1;
        colors[ImGuiCol.TitleBgActive] = palette1;
        colors[ImGuiCol.TitleBgCollapsed] = palette1;
        colors[ImGuiCol.MenuBarBg] = palette0;
        colors[ImGuiCol.ScrollbarBg] = palette0;
        colors[ImGuiCol.ScrollbarGrab] = palette3;
        colors[ImGuiCol.ScrollbarGrabHovered] = palette4;
        colors[ImGuiCol.ScrollbarGrabActive] = palette2;
        colors[ImGuiCol.CheckMark] = palette6;
        colors[ImGuiCol.SliderGrab] = palette4;
        colors[ImGuiCol.SliderGrabActive] = palette3;
        colors[ImGuiCol.Button] = palette3;
        colors[ImGuiCol.ButtonHovered] = palette4;
        colors[ImGuiCol.ButtonActive] = palette2;
        colors[ImGuiCol.Header] = palette4;
        colors[ImGuiCol.HeaderHovered] = palette3;
        colors[ImGuiCol.HeaderActive] = palette0;
        colors[ImGuiCol.Separator] = palette5;
        colors[ImGuiCol.SeparatorHovered] = palette6;
        colors[ImGuiCol.SeparatorActive] = palette6;
        colors[ImGuiCol.ResizeGrip] = palette4;
        colors[ImGuiCol.ResizeGripHovered] = palette5;
        colors[ImGuiCol.ResizeGripActive] = palette3;
        colors[ImGuiCol.Tab] = palette2;
        colors[ImGuiCol.TabHovered] = palette3;
        colors[ImGuiCol.DockingPreview] = palette4;
        colors[ImGuiCol.DockingEmptyBg] = palette6;
        colors[ImGuiCol.PlotLines] = palette5;
        colors[ImGuiCol.PlotLinesHovered] = palette6;
        colors[ImGuiCol.PlotHistogram] = palette5;
        colors[ImGuiCol.PlotHistogramHovered] = palette6;
        colors[ImGuiCol.TextSelectedBg] = palette4;
        colors[ImGuiCol.DragDropTarget] = palette4;
        colors[ImGuiCol.NavHighlight] = palette3;
        colors[ImGuiCol.NavWindowingHighlight] = palette2;
        colors[ImGuiCol.NavWindowingDimBg] = palette2;
        colors[ImGuiCol.ModalWindowDimBg] = palette2;
    }

    private static void setLightMode(float[] backgroundColor, ImVec4[] colors) {
        backgroundColor[0] = 0.94f;
        backgroundColor[1] = 0.94f;
        backgroundColor[2] = 0.94f;

        colors[ImGuiCol.Text] = new ImVec4(0.00f, 0.00f, 0.00f, 1);
        colors[ImGuiCol.TextDisabled] = new ImVec4(0.60f, 0.60f, 0.60f, 1);
        colors[ImGuiCol.WindowBg] = new ImVec4(0.94f, 0.94f, 0.94f, 1);
        colors[ImGuiCol.PopupBg] = new ImVec4(1.00f, 1.00f, 1.00f, 1);
        colors[ImGuiCol.Border] = new ImVec4(0.00f, 0.00f, 0.00f, 1);
        colors[ImGuiCol.BorderShadow] = new ImVec4(1.00f, 1.00f, 1.00f, 1);
        colors[ImGuiCol.FrameBg] = new ImVec4(1.00f, 1.00f, 1.00f, 1);
        colors[ImGuiCol.FrameBgHovered] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.FrameBgActive] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.TitleBg] = new ImVec4(0.96f, 0.96f, 0.96f, 1);
        colors[ImGuiCol.TitleBgCollapsed] = new ImVec4(1.00f, 1.00f, 1.00f, 1);
        colors[ImGuiCol.TitleBgActive] = new ImVec4(0.82f, 0.82f, 0.82f, 1);
        colors[ImGuiCol.MenuBarBg] = new ImVec4(0.86f, 0.86f, 0.86f, 1);
        colors[ImGuiCol.ScrollbarBg] = new ImVec4(0.98f, 0.98f, 0.98f, 1);
        colors[ImGuiCol.ScrollbarGrab] = new ImVec4(0.69f, 0.69f, 0.69f, 1);
        colors[ImGuiCol.ScrollbarGrabHovered] = new ImVec4(0.59f, 0.59f, 0.59f, 1);
        colors[ImGuiCol.ScrollbarGrabActive] = new ImVec4(0.49f, 0.49f, 0.49f, 1);
        colors[ImGuiCol.CheckMark] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.SliderGrab] = new ImVec4(0.24f, 0.52f, 0.88f, 1);
        colors[ImGuiCol.SliderGrabActive] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.Button] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.ButtonHovered] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.ButtonActive] = new ImVec4(0.06f, 0.53f, 0.98f, 1);
        colors[ImGuiCol.Header] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.HeaderHovered] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.HeaderActive] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.ResizeGrip] = new ImVec4(1.00f, 1.00f, 1.00f, 1);
        colors[ImGuiCol.ResizeGripHovered] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.ResizeGripActive] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
        colors[ImGuiCol.PlotLines] = new ImVec4(0.39f, 0.39f, 0.39f, 1);
        colors[ImGuiCol.PlotLinesHovered] = new ImVec4(1.00f, 0.43f, 0.35f, 1);
        colors[ImGuiCol.PlotHistogram] = new ImVec4(0.90f, 0.70f, 0.00f, 1);
        colors[ImGuiCol.PlotHistogramHovered] = new ImVec4(1.00f, 0.60f, 0.00f, 1);
        colors[ImGuiCol.TextSelectedBg] = new ImVec4(0.26f, 0.59f, 0.98f, 1);
    }
}
