package com.pine.service.streaming.texture;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.TextureStreamableResource;
import com.pine.service.streaming.AbstractStreamableService;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@PBean
public class TextureService extends AbstractStreamableService<TextureStreamableResource, TextureStreamData> {

    @Override
    protected void bindInternal() {

    }

    @Override
    public void unbind() {

    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }

    @Override
    public TextureStreamData stream(TextureStreamableResource instance) {
        ByteBuffer imageBuffer;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            imageBuffer = STBImage.stbi_load(engine.getResourceTargetDirectory() + instance.pathToFile, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (imageBuffer == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        return new TextureStreamData(width, height, imageBuffer);
    }
}
