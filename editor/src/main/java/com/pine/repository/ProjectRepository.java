package com.pine.repository;

import com.pine.PBean;
import com.pine.service.ProjectDTO;

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