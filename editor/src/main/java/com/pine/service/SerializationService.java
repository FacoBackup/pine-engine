package com.pine.service;

import com.pine.SerializableRepository;
import com.pine.SerializationState;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PostCreation;
import com.pine.messaging.Loggable;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pine.service.ProjectService.CONFIG_NAME;
import static com.pine.service.ProjectService.IDENTIFIER;

@PBean
public class SerializationService implements Loggable {
    private static final String FILE_FORMAT = ".dat";

    @PInject
    public MessageRepository messageRepository;

    @PInject
    public List<SerializableRepository> serializableRepositories;

    private final Map<String, SerializableRepository> repositoryMap = new HashMap<>();

    private boolean isDeserializationDone = false;

    @PostCreation
    public void onInitialize() {
        for (SerializableRepository repository : serializableRepositories) {
            repositoryMap.put(DigestUtils.sha1Hex(repository.getClass().getSimpleName()) + FILE_FORMAT, repository);
        }
    }

    public void writeProjectMetadata(String projectDirectory) {
        try {
            Files.write(Path.of(CONFIG_NAME), projectDirectory.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Path.of(projectDirectory + File.separator + IDENTIFIER), new Date().toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            getLogger().error("Could not save project information", e);
        }
    }

    public void serialize(String projectDirectory) {
        getLogger().info("Beginning project serialization to {}", projectDirectory);
        long start = System.currentTimeMillis();
        try {
            for (var repositoryEntry : repositoryMap.entrySet()) {
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(projectDirectory + File.separator + repositoryEntry.getKey()))) {
                    out.writeObject(repositoryEntry.getValue());
                }
            }
            long end = System.currentTimeMillis() - start;
            getLogger().info("Serialization took {}ms", end);
        } catch (Exception e) {
            messageRepository.pushMessage("Could not save project", MessageSeverity.ERROR);
            getLogger().error("Could not save project", e);
            return;
        }
        messageRepository.pushMessage("Project saved", MessageSeverity.SUCCESS);
    }

    public void deserialize(String projectDirectory) {
        getLogger().info("Loading project from {}", projectDirectory);
        long start = System.currentTimeMillis();
        isDeserializationDone = false;
        Thread thread = new Thread(() -> {
            try {
                for (String repositoryId : repositoryMap.keySet()) {
                    loadRepository(projectDirectory, repositoryId);
                }
                getLogger().info("Project load took {}ms", System.currentTimeMillis() - start);
            } catch (Exception e) {
                messageRepository.pushMessage("Error while loading project", MessageSeverity.ERROR);
                getLogger().error(e.getMessage(), e);
            }
            isDeserializationDone = true;
        });

        thread.start();
    }

    private void loadRepository(String projectDirectory, String repositoryId) {
        try {
            File file = new File(projectDirectory + File.separator + repositoryId);
            if (!file.exists()) {
                return;
            }
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()))) {
                repositoryMap.get(repositoryId).merge(in.readObject());
            }
            SerializationState.loaded.clear();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    public boolean isDeserializationDone() {
        return isDeserializationDone;
    }
}
