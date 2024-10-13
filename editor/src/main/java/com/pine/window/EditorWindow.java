package com.pine.window;

import com.pine.Engine;
import com.pine.core.AbstractWindow;
import com.pine.core.WindowService;
import com.pine.core.dock.*;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.header.EditorHeaderPanel;
import com.pine.panels.ToasterPanel;
import com.pine.repository.EditorRepository;
import com.pine.service.ProjectService;
import com.pine.service.ThemeService;
import com.pine.service.serialization.SerializationService;
import com.pine.tools.ToolsModule;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiKey;

import java.util.Collections;
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
        try {
            DockDTO dockCenter = new DockDTO(EditorDock.Viewport);
            DockDTO rightUp = new DockDTO(EditorDock.Hierarchy);
            DockDTO rightDown = new DockDTO(EditorDock.Inspector);
            DockDTO downLeft = new DockDTO(EditorDock.Console);
            DockDTO downRight = new DockDTO(EditorDock.Files);

            dockCenter.setSizeRatioForNodeAtDir(0.17f);
            rightUp.setSizeRatioForNodeAtDir(0.4f);
            rightDown.setSizeRatioForNodeAtDir(0.6f);
            downLeft.setSizeRatioForNodeAtDir(0.22f);
            downRight.setSizeRatioForNodeAtDir(0.5f);

            dockService.setDockGroupTemplate(new DockGroup("Viewport", dockCenter, List.of(downLeft, downRight), Collections.emptyList(), List.of(rightUp, rightDown)));
            dockService.createDockGroup();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
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
        start();
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
        end();
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