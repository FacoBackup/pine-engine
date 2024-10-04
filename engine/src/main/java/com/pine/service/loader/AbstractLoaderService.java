package com.pine.service.loader;

import com.pine.Loggable;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import org.lwjgl.BufferUtils;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class AbstractLoaderService implements Loggable {

    public abstract AbstractLoaderResponse load(LoadRequest resource, @Nullable AbstractLoaderExtraInfo extraInfo);

    public abstract StreamableResourceType getResourceType();

    @Nullable
    protected ByteBuffer loadStaticResource(String path) {
        try {
            try (InputStream inputStream = LoaderService.class.getClassLoader().getResourceAsStream(path)) {
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
