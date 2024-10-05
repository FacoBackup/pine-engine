package com.pine.service;

import com.pine.*;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;
import com.pine.injection.PostCreation;
import com.pine.repository.ContentBrowserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;

@PBean
public class ProjectService implements Loggable {
    private static final String IDENTIFIER = "project.pine";
    private static final String CONFIG_NAME = System.getProperty("user.home") + File.separator + IDENTIFIER;

    @PInject
    public MessageRepository messageRepository;

    @PInject
    public FSService fsService;

    @PInject
    public PInjector injector;

    @PInject
    public NativeDialogService nativeDialogService;

    @PInject
    public ProjectStateRepository projectStateRepository;

    @PInject
    public ContentBrowserRepository contentBrowserRepository;

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
                projectDirectory = System.getProperty("user.home") + File.separator + "pineEngineDefaultProject";
                fsService.createDirectory(projectDirectory);
            }
        }
    }

    public void loadProject() {

        var t = new Thread(() -> {
            try {
                File file = new File(projectDirectory + File.separator + getRepositoryIdentifier());
                if (!file.exists()) {
                    return;
                }
                messageRepository.pushMessage("Loading project", MessageSeverity.WARN);
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()))) {
                    var bean = (SerializableRepository) injector.getBean(ProjectStateRepository.class);
                    ProjectStateRepository o = (ProjectStateRepository) in.readObject();
                    bean.merge(o);
                }
                messageRepository.pushMessage("Project loaded successfully", MessageSeverity.SUCCESS);
                SerializationState.loaded.clear();
            } catch (Exception e) {
                messageRepository.pushMessage("Error while loading project", MessageSeverity.ERROR);
                getLogger().error(e.getMessage(), e);
            }
        });
        t.start();
    }

    public void save() {
        writeProject();

        try {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(projectDirectory + File.separator + getRepositoryIdentifier()))) {
                out.writeObject(projectStateRepository);
            }
        } catch (Exception e) {
            messageRepository.pushMessage("Could not save project", MessageSeverity.ERROR);
            getLogger().warn("Could not save project", e);
            return;
        }
        messageRepository.pushMessage("Project saved", MessageSeverity.SUCCESS);
    }

    private void writeProject() {
        try {
            Files.write(Path.of(CONFIG_NAME), projectDirectory.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Path.of(projectDirectory + File.separator + IDENTIFIER), new Date().toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            getLogger().warn("Could not save project to {}", CONFIG_NAME, e);
        }
    }

    private @NotNull String getRepositoryIdentifier() {
        return DigestUtils.sha1Hex(ProjectStateRepository.class.getCanonicalName()) + ".dat";
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
        writeProject();
        injector.boot();
    }

    public void newProject() {
        saveAndRestart(nativeDialogService.selectDirectory());
    }

    public String getProjectDirectory() {
        return null;
    }
}
