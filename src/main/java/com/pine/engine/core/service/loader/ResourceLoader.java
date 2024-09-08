package com.pine.engine.core.service.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Updatable;
import com.pine.engine.Engine;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.service.loader.impl.AudioLoader;
import com.pine.engine.core.service.loader.impl.MeshLoader;
import com.pine.engine.core.service.loader.impl.TextureLoader;
import com.pine.engine.core.service.loader.impl.response.AudioLoaderResponse;
import com.pine.engine.core.service.loader.impl.info.ILoaderExtraInfo;
import com.pine.engine.core.service.loader.impl.response.MeshLoaderResponse;
import com.pine.engine.core.service.loader.impl.response.TextureLoaderResponse;
import com.pine.engine.core.service.serialization.SerializableRepository;
import jakarta.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pine.engine.core.service.resource.ResourceService.MAX_TIMEOUT;

public class ResourceLoader extends SerializableRepository implements Updatable {
    private final List<AbstractLoaderResponse> loadedResources = new ArrayList<>();
    private final List<AbstractResourceLoader> resourceLoaders = new ArrayList<>();
    private final ClockRepository clock;
    private long sinceLastCleanup = 0;

    public ResourceLoader(Engine engine) {
        clock = engine.getClock();
        resourceLoaders.add(new AudioLoader(engine));
        resourceLoaders.add(new TextureLoader(engine));
        resourceLoaders.add(new MeshLoader(engine));
    }

    public AbstractLoaderResponse load(String path, boolean isStaticResource, @Nullable ILoaderExtraInfo extraInfo) {
        var dto = new LoadRequest(path, isStaticResource, extraInfo);
        final String extension = path.substring(path.lastIndexOf(".") + 1);
        AbstractLoaderResponse metadata = null;
        for (AbstractResourceLoader i : resourceLoaders) {
            if (i.getResourceType().getFileExtensions().indexOf(extension) > 0) {
                if (extraInfo != null && extraInfo.getResourceType() == i.getResourceType()) {
                    metadata = i.load(dto, extraInfo);
                } else {
                    metadata = i.load(dto, null);
                }

                if (metadata.isLoaded()) {
                    loadedResources.add(metadata);
                }
            }
        }

        if (metadata == null) {
            metadata = new AbstractLoaderResponse(false, path) {
            };
        }

        return metadata;
    }

    @Override
    protected void parseInternal(JsonElement data) {
        data.getAsJsonArray().forEach(a -> {
            JsonObject obj = a.getAsJsonObject();
            AbstractLoaderResponse instance = null;
            if (Objects.equals(obj.get(CLASS_KEY).getAsString(), TextureLoaderResponse.class.getName())) {
                instance = new TextureLoaderResponse();
            } else if (Objects.equals(obj.get(CLASS_KEY).getAsString(), MeshLoaderResponse.class.getName())) {
                instance = new MeshLoaderResponse();
            } else if (Objects.equals(obj.get(CLASS_KEY).getAsString(), AudioLoaderResponse.class.getName())) {
                instance = new AudioLoaderResponse();
            }

            if (instance != null) {
                instance.parse(obj);
                loadedResources.add(instance);
            }
        });
    }

    @Override
    public JsonElement serializeData() {
        JsonArray jsonElements = new JsonArray();
        loadedResources.forEach(a -> {
            jsonElements.add(a.serialize());
        });
        return jsonElements;
    }

    public AudioLoader getAudioLoader() {
        return (AudioLoader) resourceLoaders.getFirst();
    }

    public TextureLoader getTextureLoader() {
        return (TextureLoader) resourceLoaders.get(1);
    }

    public MeshLoader getMeshLoader() {
        return (MeshLoader) resourceLoaders.get(2);
    }

    public List<AbstractLoaderResponse> getLoadedResources() {
        return loadedResources;
    }

    @Override
    public void tick() {
        if ((clock.totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = clock.totalTime;
            ArrayList<AbstractLoaderResponse> resources = new ArrayList<>(loadedResources);
            resources.forEach(r -> {
                if (!(new File(r.getFilePath())).exists()) {
                    loadedResources.remove(r);
                }
            });
        }
    }

    @Override
    public void onInitialize() {
        load("plane.glb", true, null);
    }
}
