package com.pine.app.projects;

import com.pine.app.core.RuntimeWindow;
import com.pine.app.projects.panel.ProjectListPanel;

import java.util.ArrayList;
import java.util.List;

public class ProjectsWindow extends RuntimeWindow {
    private final List<ProjectDTO> data = new ArrayList<>();

    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(new ProjectListPanel(data));
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