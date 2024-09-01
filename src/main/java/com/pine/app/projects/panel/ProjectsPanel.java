package com.pine.app.projects.panel;

import com.pine.app.ProjectService;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.ButtonView;
import com.pine.app.core.ui.view.RepeatingView;
import com.pine.app.core.ui.view.WindowView;
import com.pine.app.ProjectDTO;
import com.pine.common.Inject;

import java.util.List;


public class ProjectsPanel extends AbstractPanel {
    @Inject
    public ProjectService projectService;

    private final List<ProjectDTO> data;

    public ProjectsPanel(List<ProjectDTO> data) {
        this.data = data;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var list = (RepeatingView) getElementById("list");
        var window = (WindowView) list.getParent();
        window.setDimensions(getWindowDimensions());

        var create = (ButtonView) getElementById("newProject");
        create.setOnClick(() -> {
            data.add(projectService.createNewProject());
        });
        list.setGetView((item) -> new ProjectRowPanel((ProjectDTO) item, this::removeProject));
        list.setData(data);
    }

    private void removeProject(ProjectDTO projectDTO) {
        projectService.deleteProject(projectDTO);
        data.remove(projectDTO);
    }
}
