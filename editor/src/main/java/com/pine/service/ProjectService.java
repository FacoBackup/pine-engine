package com.pine.service;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;
import com.pine.injection.PostCreation;
import com.pine.messaging.Loggable;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import com.pine.service.serialization.SerializationService;

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

    @PInject
    public MessageRepository messageRepository;

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
        serializationService.serialize(projectDirectory);
        messageRepository.pushMessage("Project saved", MessageSeverity.SUCCESS);
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
        serializationService.serialize(projectDirectory);
    }
}
