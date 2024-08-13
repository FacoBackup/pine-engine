package com.jengine.app.view.core;

import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.view.core.window.AbstractWindow;
import com.jengine.app.view.core.window.Configuration;
import imgui.*;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;

public abstract class RuntimeWindow extends AbstractWindow {

    protected ImFont roboto;
    protected ImFont material;
    private final String windowTitle;

    protected RuntimeWindow(String windowTitle) {
        super();
        this.windowTitle = windowTitle;
    }

    @Override
    public void configure(final Configuration config) {
        config.setTitle(windowTitle);
    }

    @Override
    public void preRun() {
    }

    @Override
    public void postRun() {
    }

    @Override
    protected void initialize(final Configuration config) {
        super.initialize(config);

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
    }

    @Override
    protected void initFonts() throws ResourceRuntimeException {
        final ImGuiIO io = ImGui.getIO();
        io.getFonts().addFontDefault();
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesDefault());
        rangesBuilder.addRanges(new short[]{(short) 0xe005, (short) 0xf8ff, 0});

        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setMergeMode(true);

        final short[] glyphRanges = rangesBuilder.buildRanges();
        material = io.getFonts().addFontFromMemoryTTF(loadFromResources("icons/MaterialIcons-Regular.ttf"), 14, fontConfig, glyphRanges);
        roboto = io.getFonts().addFontFromMemoryTTF(loadFromResources("roboto/Roboto-Regular.ttf"), 14, fontConfig, glyphRanges);
        io.getFonts().build();
    }

    @Override
    public void process() {
        if (ImGui.begin("T", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.pushFont(roboto);
            ImGui.pushFont(material);
            renderUI();
            ImGui.popFont();
            ImGui.popFont();
        }
        ImGui.end();
    }

    public abstract void renderUI();
}