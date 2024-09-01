package com.pine.app.core;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.window.AbstractWindow;
import com.pine.common.fs.FSUtil;
import imgui.*;
import imgui.flag.ImGuiConfigFlags;

public abstract class RuntimeWindow extends AbstractWindow {
//    protected ImFont roboto;
//    protected ImFont material;
    private final AbstractPanel root = new AbstractPanel() {
        @Override
        public void onInitialize() {
        }
    };

    @Override
    public void onInitialize() {
        super.onInitialize();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.setConfigViewportsNoTaskBarIcon(true);
    }

    @Override
    protected void initFonts() throws RuntimeException {
//        final ImGuiIO io = ImGui.getIO();
//        io.getFonts().addFontDefault();
//        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
//        rangesBuilder.addRanges(io.getFonts().getGlyphRangesDefault());
//        rangesBuilder.addRanges(new short[]{(short) 0xe005, (short) 0xf8ff, 0});
//
//        final ImFontConfig fontConfig = new ImFontConfig();
//        fontConfig.setMergeMode(true);
//
//        final short[] glyphRanges = rangesBuilder.buildRanges();
//        material = io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("icons/MaterialIcons-Regular.ttf"), 14, fontConfig, glyphRanges);
//        roboto = io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("roboto/Roboto-Regular.ttf"), 14, fontConfig, glyphRanges);
//        io.getFonts().build();
    }

    @Override
    public void render() {
//        ImGui.pushFont(roboto);
//        ImGui.pushFont(material);

        root.render();

//        ImGui.popFont();
//        ImGui.popFont();
    }

    protected void appendChild(View view) {
        root.appendChild(view);
    }
}