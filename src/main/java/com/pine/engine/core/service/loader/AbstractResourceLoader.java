package com.pine.engine.core.service.loader;

import com.pine.common.Loggable;
import com.pine.engine.Engine;
import com.pine.engine.core.service.loader.impl.info.ILoaderExtraInfo;
import com.pine.engine.core.service.resource.resource.ResourceType;
import jakarta.annotation.Nullable;
import org.lwjgl.BufferUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class AbstractResourceLoader implements Loggable {
    protected final Engine engine;

    public AbstractResourceLoader(Engine engine) {
        this.engine = engine;
    }

    public abstract AbstractLoaderResponse load(LoadRequest resource, @Nullable ILoaderExtraInfo extraInfo);

    public abstract ResourceType getResourceType();

    @Nullable
    protected ByteBuffer loadStaticResource(String path) {
        try {
            try (InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(path)) {
                if(inputStream != null) {
                    byte[] bytes = inputStream.readAllBytes();
                    ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bytes.length + 1);
                    byteBuffer.put(bytes);
                    byteBuffer.put((byte) 0);
                    byteBuffer.flip();

                    return byteBuffer;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
