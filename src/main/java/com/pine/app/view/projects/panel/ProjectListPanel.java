package com.pine.app.view.projects.panel;

import com.pine.app.view.component.panel.AbstractPanel;
import com.pine.app.view.component.view.RepeatingView;
import com.pine.app.view.projects.ProjectDTO;

import java.util.List;


public class ProjectListPanel extends AbstractPanel {
    private final List<ProjectDTO> data;

    public ProjectListPanel(List<ProjectDTO> data) {
        this.data = data;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var list = (RepeatingView) getElementById("list");
        list.setGetView((item) -> new ProjectRowPanel((ProjectDTO) item, this::openProject, this::removeProject));
        list.setData(data);
    }

    private void removeProject(ProjectDTO projectDTO) {

    }

    private void openProject(ProjectDTO projectDTO) {

    }

}
