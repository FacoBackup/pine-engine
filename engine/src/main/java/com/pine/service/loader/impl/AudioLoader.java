package com.pine.service.loader.impl;

import com.pine.injection.PBean;
import com.pine.service.loader.AbstractResourceLoader;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.loader.impl.response.AudioLoaderResponse;
import com.pine.service.resource.resource.ResourceType;

import javax.annotation.Nullable;
import java.util.Collections;

@PBean
public class AudioLoader extends AbstractResourceLoader {

    @Override
    public AbstractLoaderResponse load(LoadRequest resource, @Nullable AbstractLoaderExtraInfo extraInfo) {
        return new AudioLoaderResponse(false, resource, Collections.emptyList());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
