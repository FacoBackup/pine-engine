package com.pine.service.loader.impl;

import com.pine.annotation.EngineInjectable;
import com.pine.service.loader.AbstractResourceLoader;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.loader.impl.response.AudioLoaderResponse;
import com.pine.service.resource.resource.ResourceType;
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
