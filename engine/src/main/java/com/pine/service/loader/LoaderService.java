package com.pine.service.loader;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Responsible for parsing data and storing it locally while registering the StreamableResource on StreamableResourceRepository
 */
@PBean
public class LoaderService {

    @PInject
    public List<AbstractLoaderService> resourceLoaders;

    @Nullable
    public AbstractLoaderResponse load(String path, boolean isStaticResource) {
        return load(path, isStaticResource, null);
    }

    @Nullable
    public AbstractLoaderResponse load(String path, boolean isStaticResource, @Nullable AbstractLoaderExtraInfo extraInfo) {
        var dto = new LoadRequest(path, isStaticResource, extraInfo);
        return load(dto);
    }

    @Nullable
    public AbstractLoaderResponse load(LoadRequest loadRequest) {
        final String extension = loadRequest.path().substring(loadRequest.path().lastIndexOf(".") + 1);
        for (AbstractLoaderService i : resourceLoaders) {
            if (i.getResourceType().getFileExtensions().indexOf(extension) > 0) {
                return process(loadRequest.extraInfo(), i, loadRequest);
            }
        }
        return null;
    }

    @NotNull
    private AbstractLoaderResponse process(@Nullable AbstractLoaderExtraInfo extraInfo, AbstractLoaderService i, LoadRequest dto) {
        AbstractLoaderResponse metadata;
        if (extraInfo != null && extraInfo.getResourceType() == i.getResourceType()) {
            metadata = i.load(dto, extraInfo);
        } else {
            metadata = i.load(dto, null);
        }
        return metadata;
    }
}
