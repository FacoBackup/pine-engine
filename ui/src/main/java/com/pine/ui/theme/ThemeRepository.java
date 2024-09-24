package com.pine.ui.theme;

import com.pine.*;
import com.pine.inspection.InspectableRepository;
import com.pine.inspection.MutableField;
import imgui.*;
import imgui.flag.ImGuiCol;

@PBean
public class ThemeRepository implements Updatable, InspectableRepository {
    public static final ImVec4 ACCENT_COLOR = new ImVec4(0.26f, 0.59f, 0.98f, 1);

    @MutableField(label = "Accent color")
    public final ImVec4 accentColor = new ImVec4(0.26f, 0.59f, 0.98f, 1);
    public ImVec4 neutralPalette;
    public ImVec4 palette0;
    public ImVec4 palette1;
    public ImVec4 palette2;
    public ImVec4 palette3;
    public ImVec4 palette4;
    public ImVec4 palette5;
    public ImVec4 palette6;
    public final float[] backgroundColor = new float[]{.0f, .0f, .0f};
    private boolean initialized;
    public boolean isDarkMode = true;
    private boolean previousTheme = false;

    @Override
    public void tick() {
        if (previousTheme == isDarkMode) {
            return;
        }
        previousTheme = isDarkMode;

        ImGuiStyle style = ImGui.getStyle();
        ImVec4[] colors = style.getColors();

        if (!isDarkMode) {
            ImGui.styleColorsLight();
            setLightMode();
        } else {
            ImGui.styleColorsDark();
            setDarkMode();
        }

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

    private void setDarkMode() {
        palette0 = new ImVec4(10f / 255f, 10f / 255f, 10f / 255f, 1);
        palette1 = new ImVec4(18f / 255f, 18f / 255f, 18f / 255f, 1);
        palette2 = neutralPalette = new ImVec4(22f / 255f, 22f / 255f, 22f / 255f, 1);
        palette3 = new ImVec4(35f / 255f, 35f / 255f, 35f / 255f, 1);
        palette4 = new ImVec4(65f / 255f, 65f / 255f, 65f / 255f, 1);
        palette5 = new ImVec4(119f / 255f, 119f / 255f, 119f / 255f, 1);
        palette6 = new ImVec4(224f / 255f, 224f / 255f, 224f / 255f, 1);
    }

    private void setLightMode() {
        palette0 = new ImVec4(245f / 255f, 245f / 255f, 245f / 255f, 1); // light gray
        palette1 = new ImVec4(235f / 255f, 235f / 255f, 235f / 255f, 1); // slightly darker gray
        palette2 = neutralPalette = new ImVec4(225f / 255f, 225f / 255f, 225f / 255f, 1); // medium gray
        palette3 = new ImVec4(200f / 255f, 200f / 255f, 200f / 255f, 1); // darker gray
        palette4 = new ImVec4(160f / 255f, 160f / 255f, 160f / 255f, 1); // even darker gray
        palette5 = new ImVec4(120f / 255f, 120f / 255f, 120f / 255f, 1); // dark gray
        palette6 = new ImVec4(10f / 255f, 10f / 255f, 10f / 255f, 1); // dark dark really dark, actually not that dark but dark
    }

    public void initialize() {
        if (this.initialized) {
            return;
        }
        initialized = true;
        applySpacing();
        applyFonts();
    }

    public void applySpacing() {
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
        style.setFrameRounding(borderRadius);
        style.setPopupRounding(borderRadius);
        style.setScrollbarRounding(9f);
        style.setGrabRounding(borderRadius);
        style.setLogSliderDeadzone(4f);
        style.setTabRounding(borderRadius);
        style.setAlpha(1);
    }

    public void applyFonts() {
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
