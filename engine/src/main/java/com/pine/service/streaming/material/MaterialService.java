package com.pine.service.streaming.material;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.data.MaterialImportData;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.streaming.scene.SceneStreamData;
import com.pine.service.streaming.texture.TextureService;
import com.pine.type.MaterialRenderingMode;

import java.util.List;
import java.util.Map;

@PBean
public class MaterialService extends AbstractStreamableService<MaterialResourceRef> {

    @PInject
    public StreamingRepository repository;

    @PInject
    public TextureService textureService;

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new MaterialResourceRef(key);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> toBeStreamedIn) {
        var importData = (MaterialImportData) FSUtil.read(pathToFile);
        if (importData == null) {
            return null;
        }
        addTexture(importData.heightMap, toBeStreamedIn);
        addTexture(importData.normal, toBeStreamedIn);
        addTexture(importData.albedo, toBeStreamedIn);
        addTexture(importData.metallic, toBeStreamedIn);
        addTexture(importData.roughness, toBeStreamedIn);
        addTexture(importData.ao, toBeStreamedIn);

        var streamData = new MaterialStreamData();
        streamData.heightMap = (TextureResourceRef) repository.streamableResources.get(importData.heightMap);
        streamData.normal = (TextureResourceRef) repository.streamableResources.get(importData.normal);
        streamData.albedo = (TextureResourceRef) repository.streamableResources.get(importData.albedo);
        streamData.metallic = (TextureResourceRef) repository.streamableResources.get(importData.metallic);
        streamData.roughness = (TextureResourceRef) repository.streamableResources.get(importData.roughness);
        streamData.ao = (TextureResourceRef) repository.streamableResources.get(importData.ao);
        streamData.useParallax = importData.useParallax;
        streamData.parallaxHeightScale = importData.parallaxHeightScale;
        streamData.parallaxLayers = importData.parallaxLayers;
        streamData.renderingMode = importData.renderingMode;
        streamData.ssrEnabled = importData.ssrEnabled;
        streamData.anisotropicRotation = importData.anisotropicRotation;
        streamData.clearCoat = importData.clearCoat;
        streamData.anisotropy = importData.anisotropy;
        streamData.sheen = importData.sheen;
        streamData.sheenTint = importData.sheenTint;

        return streamData;
    }

    private void addTexture(String importData, Map<String, StreamableResourceType> toBeStreamedIn) {
        if (importData != null) {
            toBeStreamedIn.put(importData, StreamableResourceType.TEXTURE);
            repository.streamableResources.putIfAbsent(importData, textureService.newInstance(importData));
        }
    }
}
