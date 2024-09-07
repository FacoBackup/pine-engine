package com.pine.app.projects.panel;

import com.pine.app.ProjectDTO;
import com.pine.app.ProjectService;
import com.pine.app.core.ui.panel.AbstractWindowPanel;
import com.pine.app.core.ui.view.ButtonView;
import com.pine.app.core.ui.view.RepeatingView;
import com.pine.common.Inject;

import java.util.ArrayList;
import java.util.List;


public class ProjectsPanel extends AbstractWindowPanel {
    private final List<ProjectDTO> data = new ArrayList<>();

    @Inject
    public ProjectService projectService;

    @Override
    protected String getDefinition() {
        return """
                <fragment>
                    <button id="newProject">
                        New project
                    </button>
                    <list id="list"/>
                </fragment>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        data.addAll(projectService.listAll());

        var list = (RepeatingView) getDocument().getElementById("list");
        var create = (ButtonView) getDocument().getElementById("newProject");
        create.setOnClick(() -> data.add(projectService.createNewProject()));
        list.setGetView((item) -> new ProjectRowPanel((ProjectDTO) item, this::removeProject));
        list.setData(data);
    }

    @Override
    protected String getTitle() {
        return "Projects";
    }

    private void removeProject(ProjectDTO projectDTO) {
        projectService.deleteProject(projectDTO);
        data.remove(projectDTO);
    }
}
