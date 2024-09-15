package com.pine.engine.core.service.loader;

import com.pine.common.messages.MessageCollector;
import com.pine.common.messages.MessageSeverity;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.repository.ResourceLoaderRepository;
import com.pine.engine.core.service.AbstractMultithreadedService;
import com.pine.engine.core.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.engine.core.service.loader.impl.info.LoadRequest;
import com.pine.engine.core.service.loader.impl.response.AbstractLoaderResponse;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import static com.pine.engine.core.service.resource.ResourceService.MAX_TIMEOUT;

@EngineInjectable
public class ResourceLoaderService extends AbstractMultithreadedService {
    @EngineDependency
    public ResourceLoaderRepository repository;

    @Nullable
    public AbstractLoaderResponse load(String path, boolean isStaticResource, @Nullable AbstractLoaderExtraInfo extraInfo) {
        var dto = new LoadRequest(path, isStaticResource, extraInfo);
        final String extension = path.substring(path.lastIndexOf(".") + 1);
        for (AbstractResourceLoader i : repository.resourceLoaders) {
            if (i.getResourceType().getFileExtensions().indexOf(extension) > 0) {
                return process(extraInfo, i, dto);
            }
        }
        MessageCollector.pushMessage("No loader was found for extension " + path.split("\\.")[1], MessageSeverity.ERROR);
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
                MessageCollector.pushMessage("File was successfully loaded", MessageSeverity.SUCCESS);
            }
            repository.loadedResources.add(metadata);
        }
        return metadata;
    }

    @Override
    protected int getTickIntervalMilliseconds() {
        return MAX_TIMEOUT;
    }

    @Override
    protected void tickInternal() {
        ArrayList<AbstractLoaderResponse> resources = new ArrayList<>(repository.loadedResources);
        resources.forEach(r -> {
            if (!(new File(r.getFilePath())).exists()) {
                repository.loadedResources.remove(r);
            }
        });
    }
}
