package com.pine.service.streaming.impl;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.data.EnvironmentMapStreamData;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

import static com.pine.service.environment.CubeMapWriteUtil.getPathToFile;

@PBean
public class EnvironmentMapService extends AbstractStreamableService<EnvironmentMapResourceRef> {
    @PInject
    public StreamingRepository repository;

    @PInject
    public TextureService textureService;

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.ENVIRONMENT_MAP;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        int imageSize = 0;
        ByteBuffer[] textures = new ByteBuffer[6];
        for (int i = 0; i < CubeMapFace.values().length; i++) {
            CubeMapFace face = CubeMapFace.values()[i];
            String path = getPathToFile(pathToFile, face);
            var textureData = (TextureStreamData) textureService.stream(path, Collections.emptyMap(), Collections.emptyMap());
            textures[i] = textureData.imageBuffer;
            imageSize = textureData.width;
        }

        return new EnvironmentMapStreamData(imageSize, textures);
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new EnvironmentMapResourceRef(key);
    }
}
