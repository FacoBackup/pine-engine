package com.pine.app.projects.panel;

import com.pine.app.ProjectDTO;
import com.pine.app.ProjectService;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.ButtonView;
import com.pine.app.core.ui.view.InputView;
import com.pine.common.Inject;

import java.util.function.Consumer;

public class ProjectRowPanel extends AbstractPanel {
    @Inject
    public ProjectService projectService;

    private final ProjectDTO dto;
    private final Consumer<ProjectDTO> removeProject;
    private InputView name;

    public ProjectRowPanel(ProjectDTO dto, Consumer<ProjectDTO> removeProject) {
        super();
        this.dto = dto;
        this.removeProject = removeProject;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var edit = (ButtonView) getDocument().getElementById("edit");
        var remove = (ButtonView) getDocument().getElementById("delete");
        var open = (ButtonView) getDocument().getElementById("open");

        name = (InputView) getDocument().getElementById("name");
        name.setOnChange(dto::setName);
        name.setState(dto.getName());
        name.setEnabled(false);

        edit.setOnClick(() -> {
            name.setEnabled(!name.isEnabled());
            if (name.isEnabled()) {
                edit.setInnerText("Save");
                remove.setVisible(false);
                open.setVisible(false);
            } else {
                dto.setName(name.getValue());
                edit.setInnerText("Edit");
                remove.setVisible(true);
                open.setVisible(true);
                projectService.writeProject(dto);
            }
        });
        remove.setOnClick(() -> removeProject.accept(dto));
        open.setOnClick(() -> projectService.openProject(dto));
    }
}
