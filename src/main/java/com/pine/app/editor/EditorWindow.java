package com.pine.app.editor;

import com.pine.app.ProjectService;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.core.window.DockDTO;
import com.pine.app.editor.panels.files.FilesPanel;
import com.pine.app.core.window.DockPanel;
import com.pine.common.Inject;
import imgui.flag.ImGuiDir;

import java.util.List;


public class EditorWindow extends AbstractWindow {
    @Inject
    public ProjectService projectService;

    @Override
    public void onInitialize() {
        super.onInitialize();
        DockPanel panel = new DockPanel();
        appendChild(panel);
        panel.appendChild(new FilesPanel());
    }

    @Override
    protected List<DockDTO> getDockSpaces() {
        DockDTO dockRight = new DockDTO("World");
        DockDTO dockRightDown = new DockDTO("Inspector");
        DockDTO dockDown = new DockDTO("Console");
        DockDTO dockDownRight = new DockDTO("Files");

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
}