package com.pine.app.editor;

import com.pine.app.ProjectService;
import com.pine.app.core.ui.panel.DockDTO;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.editor.panels.console.ConsolePanel;
import com.pine.app.editor.panels.files.FilesPanel;
import com.pine.app.editor.panels.inspector.InspectorPanel;
import com.pine.app.editor.panels.viewport.ViewportPanel;
import com.pine.common.InjectBean;
import com.pine.engine.Engine;
import com.pine.engine.tools.ToolsConfigurationModule;
import com.pine.engine.tools.ToolsModule;
import imgui.flag.ImGuiDir;

import java.util.List;

public class EditorWindow extends AbstractWindow {
    @InjectBean
    public ProjectService projectService;

    private Engine engine;

    @Override
    public void onInitialize() {
        super.onInitialize();
        engine = new Engine(List.of(new ToolsModule(), new ToolsConfigurationModule()), displayW, displayH);
        engine.onInitialize();
    }

    @Override
    public void tick() {
        engine.tick();
    }

    @Override
    protected List<DockDTO> getDockSpaces() {
        DockDTO dockRight = new DockDTO("Viewport", ViewportPanel.class);
        DockDTO dockRightDown = new DockDTO("Inspector", InspectorPanel.class);
        DockDTO dockDown = new DockDTO("Console", ConsolePanel.class);
        DockDTO dockDownRight = new DockDTO("Files", FilesPanel.class);

        dockRight.setOrigin(null);
        dockRight.setSplitDir(ImGuiDir.Right);
        dockRight.setSizeRatioForNodeAtDir(0.17f);
        dockRight.setOutAtOppositeDir(null);

        dockRightDown.setOrigin(dockRight);
        dockRightDown.setSplitDir(ImGuiDir.Down);
        dockRightDown.setSizeRatioForNodeAtDir(0.6f);
        dockRightDown.setOutAtOppositeDir(dockRight);

        dockDown.setOrigin(null);
        dockDown.setSplitDir(ImGuiDir.Down);
        dockDown.setSizeRatioForNodeAtDir(0.22f);
        dockDown.setOutAtOppositeDir(null);

        dockDownRight.setOrigin(dockDown);
        dockDownRight.setSplitDir(ImGuiDir.Right);
        dockDownRight.setSizeRatioForNodeAtDir(0.5f);
        dockDownRight.setOutAtOppositeDir(dockDown);

        return List.of(
                dockRight,
                dockRightDown,
                dockDown,
                dockDownRight
        );
    }

    public int getWindowWidth() {
        return 960;
    }

    public String getWindowName() {
        return projectService.getCurrentProject().getName();
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