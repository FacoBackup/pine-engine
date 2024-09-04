package com.pine.app.projects.panel;

import com.pine.app.ProjectDTO;
import com.pine.app.ProjectService;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.ButtonView;
import com.pine.app.core.ui.view.RepeatingView;
import com.pine.app.core.ui.view.TextView;
import com.pine.app.core.ui.view.WindowView;
import com.pine.common.Inject;
import imgui.flag.ImGuiWindowFlags;

import java.util.List;


public class ProjectsPanel extends AbstractPanel {
    @Inject
    public ProjectService projectService;

    private final List<ProjectDTO> data;
    private WindowView window;

    @Override
    protected String getDefinition() {
        return """
                <window id="projectWindow">
                    <button id="newProject">
                        New project
                    </button>
                    <text id="test"></text>
                    <list id="list"/>
                </window>
                """;
    }

    public ProjectsPanel(List<ProjectDTO> data) {
        super();
        this.data = data;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var list = (RepeatingView) getDocument().getElementById("list");
        window = (WindowView) getDocument().getElementById("projectWindow");
        window.setNoMove(true);
        window.setAutoResize(false);
        window.setNoCollapse(true);
        window.setNoDecoration(true);

        var create = (ButtonView) getDocument().getElementById("newProject");
        create.setOnClick(() -> {
            data.add(projectService.createNewProject());
        });
        list.setGetView((item) -> new ProjectRowPanel((ProjectDTO) item, this::removeProject));
        list.setData(data);
    }

    @Override
    public void beforeRender() {
        window.setDimensions(getDocument().getWindowDimensions());
    }

    private void removeProject(ProjectDTO projectDTO) {
        projectService.deleteProject(projectDTO);
        data.remove(projectDTO);
    }
}
