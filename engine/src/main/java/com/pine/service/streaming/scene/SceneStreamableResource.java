package com.pine.service.streaming.scene;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.LoadSceneRequest;
import com.pine.service.streaming.texture.TextureStreamData;
import com.pine.theme.Icons;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

public class SceneStreamableResource extends AbstractStreamableResource<SceneStreamData> {
    public SceneStreamableResource(String pathToFile, String id) {
        super(pathToFile, id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }

    @Override
    protected void loadInternal(SceneStreamData data) {
    }

    @Override
    protected void disposeInternal() {
    }

    @Override
    public String getIcon() {
        return Icons.inventory_2;
    }
}
