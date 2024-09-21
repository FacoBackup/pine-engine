package com.pine.service.loader;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.ResourceLoaderRepository;
import com.pine.service.MessageService;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@PBean
public class ResourceLoaderService  {
    @PInject
    public ResourceLoaderRepository repository;

    @PInject
    public MessageService messageService;

    @PInject
    public List<AbstractResourceLoader> resourceLoaders;

    @Nullable
    public AbstractLoaderResponse load(String path, boolean isStaticResource, @Nullable AbstractLoaderExtraInfo extraInfo) {
        var dto = new LoadRequest(path, isStaticResource, extraInfo);
        final String extension = path.substring(path.lastIndexOf(".") + 1);
        for (AbstractResourceLoader i : resourceLoaders) {
            if (i.getResourceType().getFileExtensions().indexOf(extension) > 0) {
                return process(extraInfo, i, dto);
            }
        }
        messageService.onMessage("No loader was found for extension " + path.split("\\.")[1], true);
        return null;
    }

    @NotNull
    private AbstractLoaderResponse process(@Nullable AbstractLoaderExtraInfo extraInfo, AbstractResourceLoader i, LoadRequest dto) {
        AbstractLoaderResponse metadata;
        if (extraInfo != null && extraInfo.getResourceType() == i.getResourceType()) {
            metadata = i.load(dto, extraInfo);
        } else {
            metadata = i.load(dto, null);
        }

        if (metadata.isLoaded()) {
            if (extraInfo == null || !extraInfo.isSilentOperation()) {
                messageService.onMessage("File was successfully loaded", false);
            }
            repository.loadedResources.add(metadata);
        }
        return metadata;
    }
}
