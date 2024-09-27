package com.pine;

import com.pine.dock.DockService;
import com.pine.panels.ToasterPanel;
import com.pine.repository.EditorSettingsRepository;
import com.pine.service.MessageRepository;
import com.pine.service.MessageSeverity;
import com.pine.component.InstancedSceneComponent;
import com.pine.component.TransformationComponent;
import com.pine.panels.EditorHeaderPanel;
import com.pine.panels.console.ConsolePanel;
import com.pine.panels.files.FilesPanel;
import com.pine.panels.hierarchy.HierarchyPanel;
import com.pine.panels.inspector.InspectorPanel;
import com.pine.panels.viewport.ViewportPanel;
import com.pine.service.ProjectDTO;
import com.pine.service.ProjectService;
import com.pine.service.world.request.AddEntityRequest;
import com.pine.repository.ThemeService;
import com.pine.tools.ToolsModule;
import com.pine.view.View;
import com.pine.dock.DockDTO;
import imgui.ImVec4;
import imgui.flag.ImGuiDir;

import java.util.List;

import static com.pine.Engine.GLSL_VERSION;

public class EditorWindow extends AbstractWindow {
    @PInject
    public ProjectService projectService;

    @PInject
    public Engine engine;

    @PInject
    public MessageRepository messageRepository;

    @PInject
    public DockService dockService;

    @PInject
    public ThemeService themeService;

    @PInject
    public EditorSettingsRepository settingsRepository;

    @Override
    public void onInitializeInternal() {
        themeService.tick();
        engine.prepare(displayW, displayH, (String message, Boolean isError) -> {
            messageRepository.pushMessage(message, isError ? MessageSeverity.ERROR : MessageSeverity.SUCCESS);
        });
        engine.addModules(List.of(new ToolsModule()));
        engine.requestTask.addRequest(new AddEntityRequest(List.of(InstancedSceneComponent.class, TransformationComponent.class)));
        appendChild(new ToasterPanel());

        DockDTO dockCenter = new DockDTO(EditorDock.Viewport, ViewportPanel.class);
        DockDTO rightUp = new DockDTO(EditorDock.Hierarchy, HierarchyPanel.class);
        DockDTO rightDown = new DockDTO(EditorDock.Inspector, InspectorPanel.class);
        DockDTO downLeft = new DockDTO(EditorDock.Console, ConsolePanel.class);
        DockDTO downRight = new DockDTO(EditorDock.Files, FilesPanel.class);

        dockCenter.setOrigin(null);
        dockCenter.setSplitDir(ImGuiDir.Right);
        dockCenter.setSizeRatioForNodeAtDir(0.17f);
        dockCenter.setOutAtOppositeDir(null);

        rightUp.setOrigin(dockCenter);
        rightUp.setSplitDir(ImGuiDir.Down);
        rightUp.setSizeRatioForNodeAtDir(0.4f);
        rightUp.setOutAtOppositeDir(dockCenter);

        rightDown.setOrigin(rightUp);
        rightDown.setSplitDir(ImGuiDir.Down);
        rightDown.setSizeRatioForNodeAtDir(0.6f);
        rightDown.setOutAtOppositeDir(rightUp);

        downLeft.setOrigin(null);
        downLeft.setSplitDir(ImGuiDir.Down);
        downLeft.setSizeRatioForNodeAtDir(0.22f);
        downLeft.setOutAtOppositeDir(null);

        downRight.setOrigin(downLeft);
        downRight.setSplitDir(ImGuiDir.Right);
        downRight.setSizeRatioForNodeAtDir(0.5f);
        downRight.setOutAtOppositeDir(downLeft);
        dockService.getCurrentDockGroup().docks.addAll(List.of(dockCenter, rightUp, rightDown, downLeft, downRight));

        dockService.setDockGroupTemplate(dockService.getCurrentDockGroup());
    }

    @Override
    protected float[] getBackgroundColor() {
        return themeService.BACKGROUND_COLOR;
    }

    @Override
    protected ImVec4 getNeutralPalette() {
        return themeService.neutralPalette;
    }

    @Override
    protected ImVec4 getAccentColor() {
        return settingsRepository.accentColor;
    }

    @Override
    protected View getHeader() {
        return new EditorHeaderPanel();
    }

    @Override
    protected String getGlslVersion() {
        return GLSL_VERSION;
    }

    public String getWindowName() {
        ProjectDTO currentProject = projectService.getCurrentProject();
        if (currentProject != null) {
            return currentProject.getName();
        }
        return "New Project - Pine Engine";
    }

    @Override
    public void tick() {
        themeService.tick();
    }

    public Engine getEngine() {
        return engine;
    }
}