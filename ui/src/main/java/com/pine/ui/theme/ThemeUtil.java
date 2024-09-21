package com.pine.ui.theme;

import com.pine.FSUtil;
import com.pine.Icon;
import imgui.*;
import imgui.flag.ImGuiCol;

public class ThemeUtil {
    public static final ImVec4 ACCENT_COLOR = new ImVec4(0.26f, 0.59f, 0.98f, 1);
    
    public static void applyTheme(boolean isDarkMode, float[] backgroundColor) {
        if (!isDarkMode) {
            ImGui.styleColorsLight();
        } else {
            ImGui.styleColorsDark();
        }

        ImGuiStyle style = ImGui.getStyle();
        ImVec4[] colors = style.getColors();

        if (!isDarkMode) {
            setLightMode(colors);
        } else {
            setDarkMode(colors);
        } 

        colors[ImGuiCol.FrameBgHovered] = ACCENT_COLOR;
        colors[ImGuiCol.FrameBgActive] = ACCENT_COLOR;
        colors[ImGuiCol.CheckMark] = ACCENT_COLOR;
        colors[ImGuiCol.SliderGrabActive] = ACCENT_COLOR;
        colors[ImGuiCol.Button] = ACCENT_COLOR;
        colors[ImGuiCol.ButtonHovered] = ACCENT_COLOR;
        colors[ImGuiCol.Header] = ACCENT_COLOR;
        colors[ImGuiCol.HeaderHovered] = ACCENT_COLOR;
        colors[ImGuiCol.HeaderActive] = ACCENT_COLOR;
        colors[ImGuiCol.ResizeGripHovered] = ACCENT_COLOR;
        colors[ImGuiCol.ResizeGripActive] = ACCENT_COLOR;
        colors[ImGuiCol.TextSelectedBg] = ACCENT_COLOR;

        backgroundColor[0] = colors[ImGuiCol.WindowBg].x;
        backgroundColor[1] = colors[ImGuiCol.WindowBg].y;
        backgroundColor[2] = colors[ImGuiCol.WindowBg].z;

        style.setColors(colors);
    }

    private static void setDarkMode(ImVec4[] colors) {
        var palette0 = new ImVec4(10f / 255f, 10f / 255f, 10f / 255f, 1);
        var palette1 = new ImVec4(18f / 255f, 18f / 255f, 18f / 255f, 1);
        var palette2 = new ImVec4(22f / 255f, 22f / 255f, 22f / 255f, 1);
        var palette3 = new ImVec4(35f / 255f, 35f / 255f, 35f / 255f, 1);
        var palette4 = new ImVec4(65f / 255f, 65f / 255f, 65f / 255f, 1);
        var palette5 = new ImVec4(119f / 255f, 119f / 255f, 119f / 255f, 1);
        var palette6 = new ImVec4(224f / 255f, 224f / 255f, 224f / 255f, 1);

        colors[ImGuiCol.Text] = palette6;
        colors[ImGuiCol.TextDisabled] = palette6;
        colors[ImGuiCol.WindowBg] = palette1;
        colors[ImGuiCol.ChildBg] = palette1;
        colors[ImGuiCol.PopupBg] = palette1;
        colors[ImGuiCol.Border] = palette3;
        colors[ImGuiCol.BorderShadow] = palette0;
        colors[ImGuiCol.FrameBg] = palette2;
        colors[ImGuiCol.TitleBg] = palette1;
        colors[ImGuiCol.TitleBgActive] = palette1;
        colors[ImGuiCol.TitleBgCollapsed] = palette1;
        colors[ImGuiCol.MenuBarBg] = palette0;
        colors[ImGuiCol.ScrollbarBg] = palette0;
        colors[ImGuiCol.ScrollbarGrab] = palette3;
        colors[ImGuiCol.ScrollbarGrabHovered] = palette4;
        colors[ImGuiCol.ScrollbarGrabActive] = palette2;
        colors[ImGuiCol.SliderGrab] = palette4;
        colors[ImGuiCol.ButtonActive] = palette2;
        colors[ImGuiCol.Separator] = palette5;
        colors[ImGuiCol.SeparatorHovered] = palette6;
        colors[ImGuiCol.SeparatorActive] = palette6;
        colors[ImGuiCol.ResizeGrip] = palette4;
        colors[ImGuiCol.Tab] = palette2;
        colors[ImGuiCol.TabHovered] = palette3;
        colors[ImGuiCol.DockingPreview] = palette4;
        colors[ImGuiCol.DockingEmptyBg] = palette6;
        colors[ImGuiCol.PlotLines] = palette5;
        colors[ImGuiCol.PlotLinesHovered] = palette6;
        colors[ImGuiCol.PlotHistogram] = palette5;
        colors[ImGuiCol.PlotHistogramHovered] = palette6;
        colors[ImGuiCol.DragDropTarget] = palette4;
        colors[ImGuiCol.NavHighlight] = palette3;
        colors[ImGuiCol.NavWindowingHighlight] = palette2;
        colors[ImGuiCol.NavWindowingDimBg] = palette2;
        colors[ImGuiCol.ModalWindowDimBg] = palette2;
    }

    private static void setLightMode(ImVec4[] colors) {
        colors[ImGuiCol.Text] = new ImVec4(0f, 0f, 0f, 1);
        colors[ImGuiCol.TextDisabled] = new ImVec4(0.60f, 0.60f, 0.60f, 1);
        colors[ImGuiCol.WindowBg] = new ImVec4(0.94f, 0.94f, 0.94f, 1);
        colors[ImGuiCol.PopupBg] = new ImVec4(1, 1, 1, 1);
        colors[ImGuiCol.Border] = new ImVec4(0.65f, 0.65f, 0.65f, 1);
        colors[ImGuiCol.BorderShadow] = new ImVec4(1, 1, 1, 1);
        colors[ImGuiCol.FrameBg] = new ImVec4(1, 1, 1, 1);
        colors[ImGuiCol.TitleBg] = new ImVec4(0.96f, 0.96f, 0.96f, 1);
        colors[ImGuiCol.TitleBgCollapsed] = new ImVec4(1, 1, 1, 1);
        colors[ImGuiCol.TitleBgActive] = new ImVec4(0.82f, 0.82f, 0.82f, 1);
        colors[ImGuiCol.MenuBarBg] = new ImVec4(0.86f, 0.86f, 0.86f, 1);
        colors[ImGuiCol.ScrollbarBg] = new ImVec4(0.98f, 0.98f, 0.98f, 1);
        colors[ImGuiCol.ScrollbarGrab] = new ImVec4(0.69f, 0.69f, 0.69f, 1);
        colors[ImGuiCol.ScrollbarGrabHovered] = new ImVec4(0.59f, 0.59f, 0.59f, 1);
        colors[ImGuiCol.ScrollbarGrabActive] = new ImVec4(0.49f, 0.49f, 0.49f, 1);
        colors[ImGuiCol.SliderGrab] = new ImVec4(0.24f, 0.52f, 0.88f, 1);
        colors[ImGuiCol.ButtonActive] = new ImVec4(0.06f, 0.53f, 0.98f, 1);
        colors[ImGuiCol.ResizeGrip] = new ImVec4(1, 1, 1, 1);
        colors[ImGuiCol.PlotLines] = new ImVec4(0.39f, 0.39f, 0.39f, 1);
        colors[ImGuiCol.PlotLinesHovered] = new ImVec4(1, 0.43f, 0.35f, 1);
        colors[ImGuiCol.PlotHistogram] = new ImVec4(0.90f, 0.70f, 0f, 1);
        colors[ImGuiCol.PlotHistogramHovered] = new ImVec4(1, 0.60f, 0f, 1);
    }

    public static void applySpacing(){
        ImGuiStyle style = ImGui.getStyle();
        float borderRadius = 3f;
        float borderWidth = 1;

        style.setWindowMinSize(new ImVec2(25f, 25f));
        style.setWindowPadding(new ImVec2(8f, 8f));
        style.setFramePadding(new ImVec2(5f, 5f));
        style.setCellPadding(new ImVec2(6f, 5f));
        style.setItemSpacing(new ImVec2(6f, 5f));
        style.setItemInnerSpacing(new ImVec2(6f, 6f));
        style.setTouchExtraPadding(new ImVec2(0f, 0f));
        style.setIndentSpacing(25f);
        style.setScrollbarSize(13f);
        style.setGrabMinSize(10f);
        style.setWindowBorderSize(borderWidth);
        style.setChildBorderSize(borderWidth);
        style.setPopupBorderSize(borderWidth);
        style.setFrameBorderSize(borderWidth);
        style.setTabBorderSize(borderWidth);
        style.setWindowRounding(0);
        style.setChildRounding(borderRadius);
        style.setFrameRounding(0f);
        style.setPopupRounding(borderRadius);
        style.setScrollbarRounding(9f);
        style.setGrabRounding(borderRadius);
        style.setLogSliderDeadzone(4f);
        style.setTabRounding(borderRadius);
        style.setAlpha(borderWidth);
    }

    public static void applyFonts(){
        final var io = ImGui.getIO();
        io.getFonts().setFreeTypeRenderer(true);

        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setPixelSnapH(true);

        io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("fonts/fa-regular-400.ttf"), 14, fontConfig, Icon.getRange());
        fontConfig.setMergeMode(true);
        io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("fonts/fa-solid-900.ttf"), 14, fontConfig, Icon.getRange());
        io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("fonts/OpenSans-Regular.ttf"), 16, fontConfig, io.getFonts().getGlyphRangesDefault());

        io.getFonts().build();
        fontConfig.destroy();
    }
}
