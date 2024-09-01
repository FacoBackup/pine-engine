package com.pine.app;

import com.google.gson.Gson;
import com.pine.app.core.service.WindowService;
import com.pine.app.editor.EditorWindow;
import com.pine.app.projects.ProjectsWindow;
import com.pine.common.Loggable;
import com.pine.common.fs.FSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService implements Loggable {

    private static final String CONFIG_NAME = "project.pine";
    private static final Gson GSON = new Gson();

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private WindowService windowService;

    @Autowired
    private FSService fsService;

    public void openProject(ProjectDTO currentProject) {
        repository.setCurrentProject(currentProject);
        windowService.closeWindow(ProjectsWindow.class);
        windowService.openWindow(EditorWindow.class);
    }

    public ProjectDTO getCurrentProject() {
        return repository.getCurrentProject();
    }

    public ProjectDTO createNewProject() {
        ProjectDTO project = new ProjectDTO();
        fsService.createDirectory(project.getPath());
        writeProject(project);
        return project;
    }

    public List<ProjectDTO> listAll() {
        List<ProjectDTO> projects = new ArrayList<>();
        List<String> directories = fsService.readDirectories(FSService.getUserRootPath());
        for (String directory : directories) {
            if (fsService.containsFile(directory, CONFIG_NAME)) {
                try {
                    String json = new String(Files.readAllBytes(Paths.get(directory + File.separator + CONFIG_NAME)));
                    projects.add(GSON.fromJson(json, ProjectDTO.class));
                } catch (Exception e) {
                    getLogger().warn("Error while reading config file: {}{}{}", CONFIG_NAME, directory, File.separator);
                }
            }
        }
        return projects;
    }

    public void deleteProject(ProjectDTO dto) {
        fsService.deleteDirectory(dto.getPath());
    }

    public void writeProject(ProjectDTO project) {
        fsService.write(GSON.toJson(project), project.getPath() + File.separator + CONFIG_NAME);
    }
}
