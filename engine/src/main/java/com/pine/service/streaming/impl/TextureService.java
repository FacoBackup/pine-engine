package com.pine.service.streaming.impl;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;
import com.pine.service.streaming.data.StreamData;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.ref.TextureResourceRef;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;

@PBean
public class TextureService extends AbstractStreamableService<TextureResourceRef> {

    @PInject
    public StreamingRepository repository;

    @PInject
    public ImporterService importerService;

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

            STBImage.stbi_set_flip_vertically_on_load(false);
            imageBuffer = STBImage.stbi_load(pathToFile, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (imageBuffer == null) {
                getLogger().error("{}: Failed to load image: {}", pathToFile, STBImage.stbi_failure_reason());
                return null;
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        return new TextureStreamData(width, height, imageBuffer, true);
    }

    public int getTotalTextureCount() {
        int total = 0;
        for (AbstractResourceRef<?> resourceRef : repository.streamed.values()) {
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

    public void writeTexture(String pathToFile, int width, int height, int textureId){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4); // 4 bytes per pixel for RGBA
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        STBImageWrite.stbi_write_png(pathToFile, width, height, 4, buffer, width * 4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}
