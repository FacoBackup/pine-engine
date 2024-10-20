package com.pine.service.streaming.impl;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.data.MaterialImportData;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.data.MaterialStreamData;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;

import java.util.Map;

@PBean
public class MaterialService extends AbstractStreamableService<MaterialResourceRef> {

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
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        var importData = (MaterialImportData) FSUtil.read(pathToFile);
        if (importData == null) {
            return null;
        }
        addTexture(importData.heightMap, schedule, streamableResources);
        addTexture(importData.normal, schedule, streamableResources);
        addTexture(importData.albedo, schedule, streamableResources);
        addTexture(importData.metallic, schedule, streamableResources);
        addTexture(importData.roughness, schedule, streamableResources);
        addTexture(importData.ao, schedule, streamableResources);

        var streamData = new MaterialStreamData();
        if (importData.heightMap != null) {
            streamData.heightMap = (TextureResourceRef) streamableResources.get(importData.heightMap);
        }
        if (importData.normal != null) {
            streamData.normal = (TextureResourceRef) streamableResources.get(importData.normal);
        }
        if (importData.albedo != null) {
            streamData.albedo = (TextureResourceRef) streamableResources.get(importData.albedo);
        }
        if (importData.metallic != null) {
            streamData.metallic = (TextureResourceRef) streamableResources.get(importData.metallic);
        }
        if (importData.roughness != null) {
            streamData.roughness = (TextureResourceRef) streamableResources.get(importData.roughness);
        }
        if (importData.ao != null) {
            streamData.ao = (TextureResourceRef) streamableResources.get(importData.ao);
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

    private void addTexture(String importData, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        if (importData != null) {
            streamableResources.putIfAbsent(importData, textureService.newInstance(importData));
            schedule.putIfAbsent(importData, StreamableResourceType.TEXTURE);
        }
    }
}
