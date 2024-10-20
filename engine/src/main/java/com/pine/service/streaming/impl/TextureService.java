package com.pine.service.streaming.impl;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.ref.TextureResourceRef;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;

@PBean
public class TextureService extends AbstractStreamableService<TextureResourceRef> {

    @PInject
    public StreamingRepository repository;

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        ByteBuffer imageBuffer;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            imageBuffer = STBImage.stbi_load(pathToFile, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (imageBuffer == null) {
                getLogger().error("{}: Failed to load image: {}", pathToFile, STBImage.stbi_failure_reason());
                return null;
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        return new TextureStreamData(width, height, imageBuffer);
    }

    public int getTotalTextureCount() {
        int total = 0;
        for (int i = 0; i < repository.streamableResources.size(); i++) {
            var resourceRef = repository.streamableResources.get(i);
            if (resourceRef.isLoaded() && resourceRef.getResourceType() == StreamableResourceType.TEXTURE) {
                total++;
            }
        }
        return total;
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new TextureResourceRef(key);
    }
}
