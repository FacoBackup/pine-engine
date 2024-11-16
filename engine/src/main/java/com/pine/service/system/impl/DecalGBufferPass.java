package com.pine.service.system.impl;

import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.WorldTile;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MaterialResourceRef;

public class DecalGBufferPass extends AbstractGBufferPass {
    private UniformDTO parallaxHeightScale;
    private UniformDTO renderIndex;
    private UniformDTO parallaxLayers;
    private UniformDTO useParallax;
    private UniformDTO anisotropicRotation;
    private UniformDTO anisotropy;
    private UniformDTO clearCoat;
    private UniformDTO sheen;
    private UniformDTO sheenTint;
    private UniformDTO renderingMode;
    private UniformDTO ssrEnabled;
    private UniformDTO fallbackMaterial;
    private UniformDTO albedoColor;
    private UniformDTO roughnessMetallic;
    private UniformDTO useAlbedoRoughnessMetallicAO;
    private UniformDTO useNormalTexture;
    private UniformDTO modelMatrix;

    @Override
    public void onInitialize() {
        super.onInitialize();
        modelMatrix = addUniformDeclaration("modelMatrix");
        renderIndex = addUniformDeclaration("renderIndex");
        albedoColor = addUniformDeclaration("albedoColor");
        roughnessMetallic = addUniformDeclaration("roughnessMetallic");
        useAlbedoRoughnessMetallicAO = addUniformDeclaration("useAlbedoRoughnessMetallicAO");
        useNormalTexture = addUniformDeclaration("useNormalTexture");
        parallaxHeightScale = addUniformDeclaration("parallaxHeightScale");
        parallaxLayers = addUniformDeclaration("parallaxLayers");
        useParallax = addUniformDeclaration("useParallax");
        anisotropicRotation = addUniformDeclaration("anisotropicRotation");
        anisotropy = addUniformDeclaration("anisotropy");
        clearCoat = addUniformDeclaration("clearCoat");
        sheen = addUniformDeclaration("sheen");
        sheenTint = addUniformDeclaration("sheenTint");
        renderingMode = addUniformDeclaration("renderingMode");
        ssrEnabled = addUniformDeclaration("ssrEnabled");
        fallbackMaterial = addUniformDeclaration("fallbackMaterial");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferDecalShader;
    }

    @Override
    protected void renderInternal() {
        prepareCall();
        meshService.setInstanceCount(0);
        meshService.bind(meshRepository.cubeMesh);
        shaderService.bindSampler2dDirect(bufferRepository.sceneDepthCopySampler, 9);
        for (WorldTile worldTile : worldService.getLoadedTiles()) {
            if (worldTile != null) {
                for (var entityId : worldTile.getEntities()) {
                    var decal = world.bagDecalComponent.get(entityId);
                    if (decal != null) {
                        MaterialResourceRef material = decal.material != null ? (MaterialResourceRef) streamingService.streamIn(decal.material, StreamableResourceType.MATERIAL) : null;
                        if (material != null) {
                            world.entityMap.get(entityId).renderIndex = engineRepository.meshesDrawn;
                            shaderService.bindInt(engineRepository.meshesDrawn, renderIndex);
                            engineRepository.meshesDrawn++;
                            shaderService.bindMat4(world.bagTransformationComponent.get(entityId).modelMatrix, modelMatrix);
                            bindMaterial(material);
                            meshService.draw();
                        }
                    }
                }
            }
        }
    }

    private void bindMaterial(MaterialResourceRef material) {
        shaderService.bindBoolean(false, fallbackMaterial);

        material.anisotropicRotationUniform = anisotropicRotation;
        material.anisotropyUniform = anisotropy;
        material.clearCoatUniform = clearCoat;
        material.sheenUniform = sheen;
        material.sheenTintUniform = sheenTint;
        material.renderingModeUniform = renderingMode;
        material.ssrEnabledUniform = ssrEnabled;
        material.parallaxHeightScaleUniform = parallaxHeightScale;
        material.parallaxLayersUniform = parallaxLayers;
        material.useParallaxUniform = useParallax;

        material.albedoColorLocation = albedoColor;
        material.roughnessMetallicLocation = roughnessMetallic;
        material.useAlbedoRoughnessMetallicAO = useAlbedoRoughnessMetallicAO;
        material.useNormalTexture = useNormalTexture;

        material.albedoLocation = 3;
        material.roughnessLocation = 4;
        material.metallicLocation = 5;
        material.aoLocation = 6;
        material.normalLocation = 7;
        material.heightMapLocation = 8;

        materialService.bindMaterial(material);
    }

    @Override
    public String getTitle() {
        return "Decals";
    }
}
