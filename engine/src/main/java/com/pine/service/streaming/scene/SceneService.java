package com.pine.service.streaming.scene;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.LoadSceneRequest;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.texture.TextureStreamData;
import com.pine.service.streaming.texture.TextureStreamableResource;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@PBean
public class SceneService extends AbstractStreamableService<SceneStreamableResource, SceneStreamData> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }

    @Override
    public SceneStreamData stream(String pathToFile) {
        return (SceneStreamData) loadFile(pathToFile);
    }
}
