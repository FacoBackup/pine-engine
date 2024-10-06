package com.pine.service.loader;

import com.pine.Engine;
import com.pine.messaging.Loggable;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.streaming.StreamLoadData;
import com.pine.service.streaming.StreamingService;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class AbstractLoaderService implements Loggable {
    @PInject
    public Engine engine;

    @PInject
    public StreamingService streamingService;

    public abstract AbstractLoaderResponse<?> load(LoadRequest resource, @Nullable AbstractLoaderExtraInfo extraInfo);

    public float persist(AbstractStreamableResource<?> resource, StreamLoadData streamData) {
        String path = engine.getResourceTargetDirectory() + resource.pathToFile;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(streamData);
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
        return new File(path).length();
    }

    public float persist(AbstractStreamableResource<?> resource, String origin) {
        String path = engine.getResourceTargetDirectory() + resource.pathToFile;
        Path source = Paths.get(origin);
        Path target = Paths.get(path);

        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return new File(origin).length();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        return -1;
    }

    public abstract StreamableResourceType getResourceType();
}
