package com.pine.app;

import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository {
    private ProjectDTO currentProject;

    public ProjectDTO getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(ProjectDTO currentProject) {
        this.currentProject = currentProject;
    }
}
