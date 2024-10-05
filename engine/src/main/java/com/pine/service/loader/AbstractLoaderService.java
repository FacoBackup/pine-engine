package com.pine.service.loader;

import com.pine.Engine;
import com.pine.Loggable;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.streaming.StreamLoadData;
import com.pine.service.streaming.StreamingService;
import org.lwjgl.BufferUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
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

    public void persist(AbstractStreamableResource<?> resource, StreamLoadData streamData) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(engine.getResourceTargetDirectory() + resource.pathToFile))) {
            out.writeObject(streamData);
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    public void persist(AbstractStreamableResource<?> resource, String origin) {
        Path source = Paths.get(origin);
        Path target = Paths.get(engine.getResourceTargetDirectory() + resource.pathToFile);

        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully!");
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    public abstract StreamableResourceType getResourceType();
}
