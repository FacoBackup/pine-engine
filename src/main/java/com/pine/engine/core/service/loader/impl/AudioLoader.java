package com.pine.engine.core.service.loader.impl;

import com.pine.engine.core.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.engine.core.service.loader.AbstractResourceLoader;
import com.pine.engine.core.service.loader.impl.info.LoadRequest;
import com.pine.engine.core.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.engine.core.service.loader.impl.response.AudioLoaderResponse;
import com.pine.engine.core.service.resource.resource.ResourceType;
import com.pine.engine.core.EngineInjectable;
import jakarta.annotation.Nullable;

import java.util.Collections;

@EngineInjectable
public class AudioLoader extends AbstractResourceLoader {

    @Override
    public AbstractLoaderResponse load(LoadRequest resource, @Nullable AbstractLoaderExtraInfo extraInfo) {
        return new AudioLoaderResponse(false, resource.path(), Collections.emptyList());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
