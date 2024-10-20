package com.pine.service.streaming.impl;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.data.MaterialImportData;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.data.MaterialStreamData;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;

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
        return StreamableResourceType.MATERIAL;
    }

    @Override
    public StreamData stream(String pathToFile) {
        var importData = (MaterialImportData) FSUtil.read(pathToFile);
        if (importData == null) {
            return null;
        }
        addTexture(importData.heightMap);
        addTexture(importData.normal);
        addTexture(importData.albedo);
        addTexture(importData.metallic);
        addTexture(importData.roughness);
        addTexture(importData.ao);

        var streamData = new MaterialStreamData();
        if (importData.heightMap != null) {
            streamData.heightMap = (TextureResourceRef) repository.streamableResources.get(importData.heightMap);
        }
        if (importData.normal != null) {
            streamData.normal = (TextureResourceRef) repository.streamableResources.get(importData.normal);
        }
        if (importData.albedo != null) {
            streamData.albedo = (TextureResourceRef) repository.streamableResources.get(importData.albedo);
        }
        if (importData.metallic != null) {
            streamData.metallic = (TextureResourceRef) repository.streamableResources.get(importData.metallic);
        }
        if (importData.roughness != null) {
            streamData.roughness = (TextureResourceRef) repository.streamableResources.get(importData.roughness);
        }
        if (importData.ao != null) {
            streamData.ao = (TextureResourceRef) repository.streamableResources.get(importData.ao);
        }
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

    private void addTexture(String importData) {
        if (importData != null) {
            repository.streamableResources.putIfAbsent(importData, textureService.newInstance(importData));
            repository.schedule.putIfAbsent(importData, StreamableResourceType.TEXTURE);
        }
    }
}
