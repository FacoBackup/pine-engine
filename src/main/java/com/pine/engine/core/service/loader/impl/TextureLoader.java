package com.pine.engine.core.service.loader.impl;

import com.pine.engine.Engine;
import com.pine.engine.core.service.loader.AbstractLoaderResponse;
import com.pine.engine.core.service.loader.AbstractResourceLoader;
import com.pine.engine.core.service.loader.LoadRequest;
import com.pine.engine.core.service.loader.impl.info.ILoaderExtraInfo;
import com.pine.engine.core.service.loader.impl.response.TextureLoaderResponse;
import com.pine.engine.core.service.resource.resource.ResourceType;
import jakarta.annotation.Nullable;

import java.util.Collections;

public class TextureLoader extends AbstractResourceLoader {

    public TextureLoader(Engine engine) {
        super(engine);
    }

    @Override
    public AbstractLoaderResponse load(LoadRequest resource, @Nullable ILoaderExtraInfo extraInfo) {
        return new TextureLoaderResponse(false, resource.path(), Collections.emptyList());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
