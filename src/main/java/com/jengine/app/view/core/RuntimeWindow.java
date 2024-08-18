package com.jengine.app.view.core;

import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.view.component.AbstractUI;
import com.jengine.app.view.core.window.AbstractWindow;
import com.jengine.app.view.core.window.WindowConfiguration;
import imgui.*;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;

public abstract class RuntimeWindow extends AbstractWindow {

    protected ImFont roboto;
    protected ImFont material;
    private final String windowTitle;
    protected AbstractUI<?> root;

    protected RuntimeWindow(String windowTitle) {
        super();
        this.windowTitle = windowTitle;
    }

    protected abstract AbstractUI<?> setupUI();

    @Override
    public void configure(final WindowConfiguration config) {
        config.setTitle(windowTitle);
    }

    @Override
    public void preRun() {
        root = setupUI();
    }

    @Override
    public void postRun() {
        root = null;
    }

    @Override
    protected void initialize(final WindowConfiguration config) {
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
        if (root != null) {
            ImGui.pushFont(roboto);
            ImGui.pushFont(material);

            root.render();

            ImGui.popFont();
            ImGui.popFont();
        }
    }
}