package com.pine.service.importer.impl;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.texture.TextureStreamableResource;
import com.pine.service.importer.AbstractImporter;

import java.util.Collections;
import java.util.List;

@PBean
public class TextureImporter extends AbstractImporter {

    @Override
    public List<AbstractStreamableResource<?>> load(String path) {
        TextureStreamableResource textureInstance = streamingService.addNew(TextureStreamableResource.class, FSUtil.getNameFromPath(path));
        if (textureInstance != null) {
            textureInstance.size = persist(textureInstance, path);
            return List.of(textureInstance);
        }
        return Collections.emptyList();
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
