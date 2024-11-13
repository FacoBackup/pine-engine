package com.pine.service.serialization;

import com.pine.FSUtil;
import com.pine.SerializableRepository;
import com.pine.SerializationState;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;

import static com.pine.service.ProjectService.CONFIG_NAME;
import static com.pine.service.ProjectService.IDENTIFIER;

@PBean
public class SerializationService implements Loggable {
    @PInject
    public MessageRepository messageRepository;

    @PInject
    public List<SerializableRepository> serializableRepositories;

    private boolean isDeserializationDone = false;

    public void writeProjectMetadata(String projectDirectory) {
        try {
            Files.write(Path.of(CONFIG_NAME), projectDirectory.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Path.of(projectDirectory + File.separator + IDENTIFIER), new Date().toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            getLogger().error("Could not save project information", e);
        }
    }

    public void serialize(String projectDirectory, boolean silent) {
        getLogger().info("Beginning project serialization to {}", projectDirectory);
        long start = System.currentTimeMillis();

        for (SerializableRepository r : serializableRepositories) {
            r.onSave();
        }

        new Thread(() -> {
            boolean success = true;
            for (SerializableRepository r : serializableRepositories) {
                success = serializeRepository(projectDirectory, r);
            }
            if (!silent) {
                if(success) {
                    messageRepository.pushMessage("Project saved", MessageSeverity.SUCCESS);
                }else{
                    messageRepository.pushMessage("Error while saving project", MessageSeverity.ERROR);
                }
            }
            long end = System.currentTimeMillis() - start;
            getLogger().warn("Serialization took {}ms", end);
        }).start();
    }

    public boolean serializeRepository(String projectDirectory, SerializableRepository r) {
        if (!FSUtil.writeJson(r, getFilePath(projectDirectory, r))) {
            getLogger().error("Could not save project");
            return false;
        }
        return true;
    }

    private String getFilePath(String projectDirectory, SerializableRepository repository) {
        return projectDirectory + File.separator + DigestUtils.sha1Hex(repository.getClass().getSimpleName()) + ".json";
    }

    public void deserialize(String projectDirectory) {
        getLogger().warn("Loading project from {}", projectDirectory);
        long start = System.currentTimeMillis();
        isDeserializationDone = false;
        Thread thread = new Thread(() -> {
            for (SerializableRepository r : serializableRepositories) {
                var data = FSUtil.readJsonSilent(getFilePath(projectDirectory, r), r.getClass());
                if (data != null) {
                    r.merge(data);
                }
            }
            SerializationState.loaded.clear();
            getLogger().warn("Deserialization took {}ms", System.currentTimeMillis() - start);
            isDeserializationDone = true;
        });
        thread.start();
    }

    public boolean isDeserializationDone() {
        return isDeserializationDone;
    }
}
