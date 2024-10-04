package com.pine.service.loader.impl;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.loader.AbstractLoaderService;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.loader.impl.response.TextureLoaderResponse;

import javax.annotation.Nullable;
import java.util.Collections;

@PBean
public class TextureLoaderService extends AbstractLoaderService {

    @Override
    public AbstractLoaderResponse load(LoadRequest resource, @Nullable AbstractLoaderExtraInfo extraInfo) {
        return new TextureLoaderResponse(false, resource, Collections.emptyList());
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
