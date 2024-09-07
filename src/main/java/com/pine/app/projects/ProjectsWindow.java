package com.pine.app.projects;

import com.pine.app.ProjectService;
import com.pine.app.core.ui.panel.DockDTO;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.projects.panel.ProjectsPanel;
import com.pine.common.Inject;
import imgui.flag.ImGuiDir;

import java.util.List;

public class ProjectsWindow extends AbstractWindow {

    @Inject
    public ProjectService projectService;

    @Override
    public void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected List<DockDTO> getDockSpaces() {
        DockDTO center = new DockDTO("Projects", ProjectsPanel.class);

        center.setOrigin(null);
        center.setSplitDir(ImGuiDir.Right);
        center.setSizeRatioForNodeAtDir(1);
        center.setOutAtOppositeDir(null);

        return List.of(center);
    }

    @Override
    protected void onBeforeRender() {
    }

    public int getWindowWidth() {
        return 960;
    }

    public String getWindowName() {
        return "Projects";
    }

    public int getWindowHeight() {
        return 540;
    }

    public boolean isFullScreen() {
        return false;
    }
}