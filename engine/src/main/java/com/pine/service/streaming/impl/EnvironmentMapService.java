package com.pine.service.streaming.impl;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.LevelOfDetail;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.data.EnvironmentMapLOD;
import com.pine.service.streaming.data.EnvironmentMapStreamData;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
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
        EnvironmentMapLOD[] lodTextures = new EnvironmentMapLOD[LevelOfDetail.values().length];
        for (int j = 0; j < LevelOfDetail.values().length; j++) {
            LevelOfDetail lod = LevelOfDetail.values()[j];
            ByteBuffer[] textures = new ByteBuffer[6];
            int imageSize = 0;
            for (int i = 0; i < CubeMapFace.values().length; i++) {
                CubeMapFace face = CubeMapFace.values()[i];
                String path = getPathToFile(pathToFile, lod, face);
                var textureData = (TextureStreamData) textureService.stream(path, Collections.emptyMap(), Collections.emptyMap());
                textures[i] = textureData.imageBuffer;
                imageSize = textureData.width;
            }
            lodTextures[j] = new EnvironmentMapLOD(textures, imageSize);
        }
        return new EnvironmentMapStreamData(lodTextures);
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new TextureResourceRef(key);
    }
}
