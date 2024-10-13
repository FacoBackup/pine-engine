package com.pine.service.serialization;

import com.pine.SerializableRepository;
import com.pine.SerializationState;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PostCreation;
import com.pine.messaging.Loggable;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

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
            repositoryMap.put(repository.getClass().getSimpleName(), repository);
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
        new Thread(() -> {
            try {
                RepositoryContainer repositoryContainer = new RepositoryContainer();
                repositoryContainer.serializables.addAll(serializableRepositories);
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(getFilePath(projectDirectory)))) {
                    out.writeObject(repositoryContainer);
                }
                long end = System.currentTimeMillis() - start;
                getLogger().warn("Serialization took {}ms", end);
            } catch (Exception e) {
                getLogger().error("Could not save project", e);
            }
        }).start();
    }

    private static @NotNull String getFilePath(String projectDirectory) {
        return projectDirectory + File.separator + DigestUtils.sha1Hex(RepositoryContainer.class.getSimpleName());
    }

    public void deserialize(String projectDirectory) {
        getLogger().warn("Loading project from {}", projectDirectory);
        long start = System.currentTimeMillis();
        isDeserializationDone = false;
        Thread thread = new Thread(() -> {
            try {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(getFilePath(projectDirectory)))) {
                    ((RepositoryContainer) in.readObject()).serializables.forEach(r -> repositoryMap.get(r.getClass().getSimpleName()).merge(r));
                }
                SerializationState.loaded.clear();
            } catch (Exception e) {
                messageRepository.pushMessage("Error while loading project", MessageSeverity.ERROR);
                getLogger().error("An error occurred while loading project", e);
            }
            getLogger().warn("Deserialization took {}ms", System.currentTimeMillis() - start);
            isDeserializationDone = true;
        });
        thread.start();
    }

    public boolean isDeserializationDone() {
        return isDeserializationDone;
    }
}
