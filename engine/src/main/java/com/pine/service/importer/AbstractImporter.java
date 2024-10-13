package com.pine.service.importer;

import com.pine.Engine;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamLoadData;
import com.pine.service.streaming.StreamingService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public abstract class AbstractImporter implements Loggable {
    @PInject
    public Engine engine;

    @PInject
    public StreamingService streamingService;

    public abstract List<AbstractStreamableResource<?>> load(String path);

    public float persist(AbstractStreamableResource<?> resource, StreamLoadData streamData) {
        log(resource);
        String path = engine.getResourceTargetDirectory() + resource.pathToFile;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(streamData);
        } catch (Exception ex) {
            getLogger().error("Error while persisting {}", resource.id, ex);
        }
        return new File(path).length();
    }

    private void log(AbstractStreamableResource<?> resource) {
        getLogger().warn("Persisting resource {} of type {}", resource.getResourceType(), resource.id);
    }

    public float persist(AbstractStreamableResource<?> resource, String origin) {
        log(resource);

        String path = engine.getResourceTargetDirectory() + resource.pathToFile;
        Path source = Paths.get(origin);
        Path target = Paths.get(path);

        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return new File(origin).length();
        } catch (Exception e) {
            getLogger().error("Error while persisting {}", resource.id, e);
        }
        return -1;
    }

    public abstract StreamableResourceType getResourceType();
}
