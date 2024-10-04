package com.pine.service.loader.impl;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.TextureStreamableResource;
import com.pine.service.loader.AbstractLoaderService;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.loader.impl.response.TextureLoaderResponse;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;

@PBean
public class TextureLoaderService extends AbstractLoaderService {

    @Override
    public AbstractLoaderResponse<?> load(LoadRequest resource, @Nullable AbstractLoaderExtraInfo extraInfo) {
        TextureStreamableResource textureInstance = streamingService.addNew(TextureStreamableResource.class, FSUtil.getNameFromPath(resource.path()));
        if (textureInstance != null) {
            persist(textureInstance, resource.path());
            return new TextureLoaderResponse(true, List.of(textureInstance));
        }
        return new TextureLoaderResponse(false, Collections.emptyList());
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
