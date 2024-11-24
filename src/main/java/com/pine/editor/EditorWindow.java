package com.pine.editor;

import com.pine.common.injection.PInject;
import com.pine.editor.core.AbstractView;
import com.pine.editor.core.AbstractWindow;
import com.pine.editor.core.WindowService;
import com.pine.editor.panels.ToasterPanel;
import com.pine.editor.panels.header.EditorHeaderPanel;
import com.pine.editor.panels.viewport.FullScreenViewportPanel;
import com.pine.editor.repository.EditorRepository;
import com.pine.editor.service.ProjectService;
import com.pine.editor.service.ThemeService;
import com.pine.editor.service.serialization.SerializationService;
import com.pine.engine.Engine;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiKey;


public class EditorWindow extends AbstractWindow {
    @PInject
    public ProjectService projectService;

    @PInject
    public Engine engine;

    @PInject
    public ThemeService themeService;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public WindowService windowService;

    @PInject
    public SerializationService serializationRepository;

    private boolean isInitialized = false;
    private FullScreenViewportPanel fullscreen;

    @Override
    public void onInitialize() {
        super.onInitialize();
        projectService.loadProject();
        appendChild(new ToasterPanel());
    }

    @Override
    public ImVec4 getNeutralPalette() {
        return themeService.neutralPalette;
    }

    @Override
    public ImVec4 getAccentColor() {
        return editorRepository.accent;
    }

    @Override
    public AbstractView getHeader() {
        return new EditorHeaderPanel();
    }

    @Override
    public void render() {
        themeService.tick();
        if (serializationRepository.isDeserializationDone()) {
            if (!isInitialized) {
                windowService.maximize();
                engine.start(windowService.getDisplayW(), windowService.getDisplayH(), projectService.getProjectDirectory());
                appendChild(fullscreen = new FullScreenViewportPanel());
                removeChild(fullscreen);
                isInitialized = true;
            }
            if (editorRepository.fullScreen) {
                fullscreen.render();
                if (ImGui.isKeyDown(ImGuiKey.Escape)) {
                    editorRepository.fullScreen = false;
                }
            } else {
                renderDockSpaces();
            }
        }
    }

    @Override
    public float getWindowScaleX() {
        return .4f;
    }

    @Override
    public float getWindowScaleY() {
        return .4f;
    }
}