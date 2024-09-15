package com.pine.app;

import com.pine.app.core.ui.panel.DockDTO;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.panels.console.ConsolePanel;
import com.pine.app.panels.files.FilesPanel;
import com.pine.app.panels.hierarchy.HierarchyPanel;
import com.pine.app.panels.inspector.InspectorPanel;
import com.pine.app.panels.viewport.ViewportPanel;
import com.pine.app.repository.EntitySelectionRepository;
import com.pine.app.service.ProjectDTO;
import com.pine.app.service.ProjectService;
import com.pine.common.InjectBean;
import com.pine.engine.Engine;
import com.pine.engine.tools.ToolsConfigurationModule;
import com.pine.engine.tools.ToolsModule;
import imgui.flag.ImGuiDir;

import java.util.List;

public class EditorWindow extends AbstractWindow {

    @InjectBean
    public ProjectService projectService;

    @InjectBean
    private EntitySelectionRepository selectionRepository;

    private Engine engine;

    @Override
    public void onInitialize() {
        super.onInitialize();
        engine = new Engine(displayW, displayH);
        engine.addModules(List.of(new ToolsModule(), new ToolsConfigurationModule(selectionRepository.getSelected())));
    }

    @Override
    protected List<DockDTO> getDockSpaces() {
        DockDTO dockCenter = new DockDTO("Viewport", ViewportPanel.class);
        DockDTO dockRightUp = new DockDTO("Hierarchy", HierarchyPanel.class);
        DockDTO dockRightDown = new DockDTO("Inspector", InspectorPanel.class);
        DockDTO dockDown = new DockDTO("Console", ConsolePanel.class);
        DockDTO dockDownRight = new DockDTO("Files", FilesPanel.class);

        dockCenter.setOrigin(null);
        dockCenter.setSplitDir(ImGuiDir.Right);
        dockCenter.setSizeRatioForNodeAtDir(0.17f);
        dockCenter.setOutAtOppositeDir(null);

        dockRightUp.setOrigin(dockCenter);
        dockRightUp.setSplitDir(ImGuiDir.Down);
        dockRightUp.setSizeRatioForNodeAtDir(0.4f);
        dockRightUp.setOutAtOppositeDir(dockCenter);

        dockRightDown.setOrigin(dockRightUp);
        dockRightDown.setSplitDir(ImGuiDir.Down);
        dockRightDown.setSizeRatioForNodeAtDir(0.6f);
        dockRightDown.setOutAtOppositeDir(dockRightUp);

        dockDown.setOrigin(null);
        dockDown.setSplitDir(ImGuiDir.Down);
        dockDown.setSizeRatioForNodeAtDir(0.22f);
        dockDown.setOutAtOppositeDir(null);

        dockDownRight.setOrigin(dockDown);
        dockDownRight.setSplitDir(ImGuiDir.Right);
        dockDownRight.setSizeRatioForNodeAtDir(0.5f);
        dockDownRight.setOutAtOppositeDir(dockDown);

        return List.of(
                dockCenter,
                dockRightUp,
                dockRightDown,
                dockDown,
                dockDownRight
        );
    }

    public int getWindowWidth() {
        return 960;
    }

    public String getWindowName() {
        ProjectDTO currentProject = projectService.getCurrentProject();
        if(currentProject != null) {
            return currentProject.getName();
        }
        return "New Project - Pine Engine";
    }

    public int getWindowHeight() {
        return 540;
    }

    public boolean isFullScreen() {
        return false;
    }

    public Engine getEngine() {
        return engine;
    }
}