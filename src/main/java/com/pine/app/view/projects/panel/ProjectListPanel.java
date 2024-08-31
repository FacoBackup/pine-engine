package com.pine.app.view.projects.panel;

import com.pine.app.view.core.component.panel.AbstractPanel;
import com.pine.app.view.core.component.view.ButtonView;
import com.pine.app.view.core.component.view.RepeatingView;
import com.pine.app.view.core.component.view.WindowView;
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
        var window = (WindowView) list.getParent();
        window.setDimensions(getWindowDimensions());

        var create = (ButtonView) getElementById("newProject");
        create.setOnClick(() -> {
            data.add(new ProjectDTO());
        });
        list.setGetView((item) -> new ProjectRowPanel((ProjectDTO) item, this::openProject, this::removeProject));
        list.setData(data);
    }

    private void removeProject(ProjectDTO projectDTO) {

    }

    private void openProject(ProjectDTO projectDTO) {

    }
}
