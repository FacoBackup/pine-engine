package com.pine.app.repository;

import com.pine.PBean;
import com.pine.app.service.ProjectDTO;

@PBean
public class ProjectRepository {
    private ProjectDTO currentProject;

    public ProjectDTO getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(ProjectDTO currentProject) {
        this.currentProject = currentProject;
    }
}
