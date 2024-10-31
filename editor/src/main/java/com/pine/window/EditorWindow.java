package com.pine.window;

import com.pine.Engine;
import com.pine.core.AbstractWindow;
import com.pine.core.WindowService;
import com.pine.core.dock.DockService;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.ToasterPanel;
import com.pine.panels.header.EditorHeaderPanel;
import com.pine.repository.EditorRepository;
import com.pine.service.ProjectService;
import com.pine.service.ThemeService;
import com.pine.service.serialization.SerializationService;
import com.pine.tools.ToolsModule;
import imgui.ImGui;
import imgui.ImVec4;

import java.util.List;

import static com.pine.core.dock.DockPanel.FLAGS;
import static com.pine.core.dock.DockPanel.OPEN;


public class EditorWindow extends AbstractWindow {
    @PInject
    public ProjectService projectService;

    @PInject
    public Engine engine;

    @PInject
    public DockService dockService;

    @PInject
    public ThemeService themeService;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public WindowService windowService;

    @PInject
    public SerializationService serializationRepository;

    private boolean isInitialized = false;

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
        startTracking();
        themeService.tick();
        if (serializationRepository.isDeserializationDone()) {
            if (!isInitialized) {
                windowService.maximize();
                engine.start(windowService.getDisplayW(), windowService.getDisplayH(), List.of(new ToolsModule()), projectService.getProjectDirectory());
                isInitialized = true;
            }
            super.render();
        } else {
            ImGui.begin("##windowLoader", OPEN, FLAGS);
            ImGui.text("Pine Engine");
            ImGui.text("Loading scene...");
            ImGui.end();
        }
        endTracking();
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