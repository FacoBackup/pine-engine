package com.pine.app.projects;

import com.pine.app.ProjectDTO;
import com.pine.app.ProjectService;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.projects.panel.ProjectsPanel;
import com.pine.common.Inject;

import java.util.ArrayList;
import java.util.List;

public class ProjectsWindow extends AbstractWindow {
    private final List<ProjectDTO> data = new ArrayList<>();

    @Inject
    public ProjectService projectService;

    @Override
    public void onInitialize() {
        super.onInitialize();
        data.addAll(projectService.listAll());
        appendChild(new ProjectsPanel(data));
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