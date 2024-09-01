package com.pine.app.editor;

import com.pine.app.ProjectService;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.editor.panels.files.FilesPanel;
import com.pine.common.Inject;


public class EditorWindow extends AbstractWindow {
    @Inject
    public ProjectService projectService;

    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(new FilesPanel());
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
}