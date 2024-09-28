package com.pine.service;

import com.pine.*;
import com.pine.repository.MessageRepository;
import com.pine.repository.MessageSeverity;
import com.pine.repository.ProjectDTO;
import com.pine.tools.tasks.WorldTreeTask;
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
public class ProjectService implements Loggable, Initializable {
    private static final String IDENTIFIER = "project.pine";
    private static final String CONFIG_NAME = System.getProperty("user.home") + File.separator + IDENTIFIER;

    @PInject
    public MessageRepository messageRepository;

    @PInject
    public FSService fsService;

    @PInject
    public PInjector injector;

    @PInject
    public WorldTreeTask treeTask;

    @PInject
    public List<SerializableRepository> serializableRepositories;

    private String previousOpenedProject = null;

    @Override
    public void onInitialize() {
        var file = new File(CONFIG_NAME);
        if (file.exists()) {
            try {
                previousOpenedProject = Files.readString(Path.of(file.getAbsolutePath()));
                if (!new File(previousOpenedProject).exists()) {
                    previousOpenedProject = null;
                }
            } catch (Exception e) {
                getLogger().warn("No previous project to be opened was found, starting as a clean instance");
            }
        }
    }

    public void loadProject() {
        if (previousOpenedProject == null) {
            return;
        }
        messageRepository.pushMessage("Loading project", MessageSeverity.WARN);
        var t = new Thread(() -> {
            try {
                List<File> files = fsService.readFilesInDirectory(previousOpenedProject);
                for (var file : files) {
                    if (file.getName().contains(IDENTIFIER)) {
                        continue;
                    }
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()))) {
                        Class<?> repoClass = Class.forName(file.getName());
                        var bean = (SerializableRepository) injector.getBean(repoClass);
                        bean.merge(in.readObject());
                    }
                }
                messageRepository.pushMessage("Project loaded successfully", MessageSeverity.SUCCESS);
                treeTask.update();
                getLogger().info("Project loaded from {} files", files.size());
            } catch (Exception e) {
                messageRepository.pushMessage("Error while loading project", MessageSeverity.ERROR);
                getLogger().error(e.getMessage(), e);
            }
        });
        t.start();
    }

    public void save() {
        if (previousOpenedProject == null) {
            previousOpenedProject = selectDirectory();
        }
        try {
            Files.write(Path.of(CONFIG_NAME), previousOpenedProject.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Path.of(previousOpenedProject + File.separator + IDENTIFIER), new Date().toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            getLogger().warn("Could not save project cache to {}", CONFIG_NAME, e);
        }

        try {
            for (SerializableRepository repository : serializableRepositories) {
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(previousOpenedProject + File.separator + repository.getClass().getCanonicalName()))) {
                    out.writeObject(repository);
                }
            }
        } catch (Exception e) {
            messageRepository.pushMessage("Could not save project", MessageSeverity.ERROR);
            getLogger().warn("Could not save project", e);
            return;
        }
        messageRepository.pushMessage("Project saved", MessageSeverity.SUCCESS);
    }

    public void openProject() {
        String selected = selectDirectory();
        if (selected != null) {
            List<File> files = fsService.readFilesInDirectory(selected);
            if (files.stream().anyMatch(a -> a.getName().contains(IDENTIFIER))) {
                previousOpenedProject = selected;
                loadProject();
            }
        }
    }

    public void newProject() {
        previousOpenedProject = null;
        // TODO - Clear injection cache and re-create editor window
    }

    private static String selectDirectory() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NativeFileDialog.NFD_Init();
            String selectedDirectory = openDirectoryDialog();
            NativeFileDialog.NFD_Quit();
            if (selectedDirectory != null) {
                return selectedDirectory;
            } else {
                return null;
            }
        }
    }

    private static String openDirectoryDialog() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer outPath = stack.mallocPointer(1);
            int result = NativeFileDialog.NFD_PickFolder(outPath, System.getProperty("user.home"));
            if (result == NativeFileDialog.NFD_OKAY) {
                String path = outPath.getStringUTF8(0);
                NativeFileDialog.nNFD_FreePath(outPath.get(0));
                return path;
            }
        }
        return null;
    }
}
