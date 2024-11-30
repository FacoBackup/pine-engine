package com.pine.engine.service.streaming.impl;

import com.pine.FSUtil;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.engine.repository.ClockRepository;
import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.repository.terrain.MaterialLayer;
import com.pine.engine.repository.terrain.MaterialLayers;
import com.pine.engine.service.importer.data.MaterialImportData;
import com.pine.engine.service.resource.shader.ShaderService;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.streaming.StreamingService;
import com.pine.engine.service.streaming.data.MaterialStreamData;
import com.pine.engine.service.streaming.data.StreamData;
import com.pine.engine.service.streaming.ref.MaterialResourceRef;
import com.pine.engine.service.streaming.ref.TextureResourceRef;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

@PBean
public class MaterialService extends AbstractStreamableService<MaterialResourceRef> {

    @PInject
    public TextureService textureService;
    @PInject
    public ShaderService shaderService;
    @PInject
    public ClockRepository clockRepository;
    @PInject
    public StreamingService streamingService;

    private final Vector4f useAlbedoRoughnessMetallicAO = new Vector4f();
    private final Vector2f roughnessMetallic = new Vector2f();

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
        var importData = (MaterialImportData) FSUtil.readBinary(pathToFile);
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
        streamData.roughnessVal = importData.roughnessVal;
        streamData.metallicVal = importData.metallicVal;
        streamData.albedoColor = importData.albedoColor;


        return streamData;
    }

    private void addTexture(String importData, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        if (importData != null) {
            streamableResources.putIfAbsent(importData, textureService.newInstance(importData));
            schedule.putIfAbsent(importData, StreamableResourceType.TEXTURE);
        }
    }

    public void bindMaterial(MaterialResourceRef request) {
        request.lastUse = clockRepository.totalTime;
        useAlbedoRoughnessMetallicAO.zero();
        if (request.albedo != null) {
            shaderService.bindSampler2dDirect(request.albedo, request.albedoLocation);
            useAlbedoRoughnessMetallicAO.x = 1;
        }
        if (request.roughness != null) {
            shaderService.bindSampler2dDirect(request.roughness, request.roughnessLocation);
            useAlbedoRoughnessMetallicAO.y = 1;
        }
        if (request.metallic != null) {
            shaderService.bindSampler2dDirect(request.metallic, request.metallicLocation);
            useAlbedoRoughnessMetallicAO.z = 1;
        }
        if (request.ao != null) {
            shaderService.bindSampler2dDirect(request.ao, request.aoLocation);
            useAlbedoRoughnessMetallicAO.w = 1;
        }
        if (request.normal != null) {
            shaderService.bindSampler2dDirect(request.normal, request.normalLocation);
            shaderService.bindBoolean(true, request.useNormalTexture);
        } else {
            shaderService.bindBoolean(false, request.useNormalTexture);
        }

        if (request.heightMap != null) {
            shaderService.bindSampler2dDirect(request.heightMap, request.heightMapLocation);
        }
        roughnessMetallic.x = request.roughnessVal;
        roughnessMetallic.y = request.metallicVal;

        shaderService.bindVec3(request.albedoColor, request.albedoColorLocation);
        shaderService.bindVec2(roughnessMetallic, request.roughnessMetallicLocation);
        shaderService.bindVec4(useAlbedoRoughnessMetallicAO, request.useAlbedoRoughnessMetallicAO);
        shaderService.bindFloat(request.anisotropicRotation, request.anisotropicRotationUniform);
        shaderService.bindFloat(request.anisotropy, request.anisotropyUniform);
        shaderService.bindFloat(request.clearCoat, request.clearCoatUniform);
        shaderService.bindFloat(request.sheen, request.sheenUniform);
        shaderService.bindFloat(request.sheenTint, request.sheenTintUniform);
        shaderService.bindInt(request.renderingMode.getId(), request.renderingModeUniform);
        shaderService.bindInt(request.ssrEnabled ? 1 : 0, request.ssrEnabledUniform);
        shaderService.bindFloat(request.parallaxHeightScale, request.parallaxHeightScaleUniform);
        shaderService.bindInt(request.parallaxLayers, request.parallaxLayersUniform);
        shaderService.bindBoolean(request.useParallax, request.useParallaxUniform);
    }

    int samplerIndex = 5;
    int matIndex = 0;

    public void bindMaterialLayers(String materialMask, MaterialLayers layers, List<UniformDTO> materialUniforms) {
        var materialMaskTexture = (TextureResourceRef) streamingService.streamIn(materialMask, StreamableResourceType.TEXTURE);
        bindTexture(materialMaskTexture, 4);

        samplerIndex = 5;
        matIndex = 0;

        bindLayer(layers.materialLayerA, materialUniforms);
        bindLayer(layers.materialLayerB, materialUniforms);
        bindLayer(layers.materialLayerC, materialUniforms);
        bindLayer(layers.materialLayerD, materialUniforms);
    }

    private void bindLayer(MaterialLayer layer, List<UniformDTO> materialUniforms) {
        var material = (MaterialResourceRef) streamingService.streamIn(layer.material, StreamableResourceType.MATERIAL);
        if (material != null) {
            bindTexture(material.albedo, samplerIndex);
            samplerIndex++;
            bindTexture(material.roughness, samplerIndex);
            samplerIndex++;
            bindTexture(material.metallic, samplerIndex);
            samplerIndex++;
            bindTexture(material.normal, samplerIndex);
            samplerIndex++;

            layer.channel.mul(layer.weight);
            shaderService.bindVec3(layer.channel, materialUniforms.get(matIndex));
            matIndex++;
        }
    }

    private void bindTexture(TextureResourceRef texture, int index) {
        if (texture != null) {
            shaderService.bindSampler2dDirect(texture.texture, index);
            texture.lastUse = clockRepository.totalTime;
        }
    }
}
