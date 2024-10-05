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
    public AbstractLoaderResponse<?> load(String path) {
        return load(path, null);
    }

    @Nullable
    public AbstractLoaderResponse<?> load(String path, @Nullable AbstractLoaderExtraInfo extraInfo) {
        var dto = new LoadRequest(path, extraInfo);
        return load(dto);
    }

    @Nullable
    public AbstractLoaderResponse<?> load(LoadRequest loadRequest) {
        final String extension = loadRequest.path().substring(loadRequest.path().lastIndexOf(".") + 1);
        for (AbstractLoaderService i : resourceLoaders) {
            if (i.getResourceType().getFileExtensions().indexOf(extension) > 0) {
                AbstractLoaderExtraInfo extra = loadRequest.extraInfo();
                return process(extra != null && extra.getResourceType() == i.getResourceType() ? extra : null, i, loadRequest);
            }
        }
        return null;
    }

    @NotNull
    private AbstractLoaderResponse<?> process(@Nullable AbstractLoaderExtraInfo extraInfo, AbstractLoaderService i, LoadRequest dto) {
        AbstractLoaderResponse<?> response;
        if (extraInfo != null && extraInfo.getResourceType() == i.getResourceType()) {
            response = i.load(dto, extraInfo);
        } else {
            response = i.load(dto, null);
        }
        return response;
    }
}
