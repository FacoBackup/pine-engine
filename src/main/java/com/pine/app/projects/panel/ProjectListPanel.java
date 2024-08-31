package com.pine.app.projects.panel;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.ButtonView;
import com.pine.app.core.ui.view.RepeatingView;
import com.pine.app.core.ui.view.WindowView;
import com.pine.app.projects.ProjectDTO;

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
