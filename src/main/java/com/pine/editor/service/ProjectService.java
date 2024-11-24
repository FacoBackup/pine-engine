package com.pine.editor.service;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.injection.PInjector;
import com.pine.common.injection.PostCreation;
import com.pine.common.messaging.Loggable;
import com.pine.editor.service.serialization.SerializationService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@PBean
public class ProjectService implements Loggable {
    public static final String IDENTIFIER = "project.pine";
    public static final String CONFIG_NAME = System.getProperty("user.home") + File.separator + IDENTIFIER;

    @PInject
    public FSService fsService;

    @PInject
    public PInjector injector;

    @PInject
    public NativeDialogService nativeDialogService;

    @PInject
    public SerializationService serializationService;

    private String projectDirectory = null;

    @PostCreation
    public void onInitialize() {
        var file = new File(CONFIG_NAME);
        if (file.exists()) {
            try {
                projectDirectory = Files.readString(Path.of(file.getAbsolutePath()));
                if (!new File(projectDirectory).exists()) {
                    projectDirectory = null;
                }
            } catch (Exception e) {
                getLogger().warn("No previous project to be opened was found, starting as a clean instance");
            }
            if (projectDirectory == null) {
                projectDirectory = System.getProperty("user.home") + File.separator + "pine-engine-default-project";
                fsService.createDirectory(projectDirectory);
            }
        }
    }

    public void loadProject() {
        serializationService.deserialize(projectDirectory);
    }

    public void save() {
        serializationService.writeProjectMetadata(projectDirectory);
        serializationService.serialize(projectDirectory, false);
    }

    public void openProject() {
        String selected = nativeDialogService.selectDirectory();
        if (selected != null) {
            List<File> files = fsService.readFilesInDirectory(selected);
            if (files.stream().anyMatch(a -> a.getName().contains(IDENTIFIER))) {
                saveAndRestart(selected);
            }
        }
    }

    private void saveAndRestart(String selected) {
        projectDirectory = selected;
        serializationService.writeProjectMetadata(projectDirectory);
        injector.boot();
    }

    public void newProject() {
        String newPath = nativeDialogService.selectDirectory();
        if(newPath != null) {
            saveAndRestart(newPath);
        }
    }

    public String getProjectDirectory() {
        return projectDirectory;
    }

    public void saveSilently() {
        serializationService.writeProjectMetadata(projectDirectory);
        serializationService.serialize(projectDirectory, true);
    }
}
