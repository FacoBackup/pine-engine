package com.pine.service.system.impl;

import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;

import java.util.List;

public class PrimitiveGBufferPass extends AbstractGBufferPass {
    private UniformDTO transformationIndex;
    private UniformDTO parallaxHeightScale;
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

    @Override
    public void onInitialize() {
        super.onInitialize();
        albedoColor = addUniformDeclaration("albedoColor");
        roughnessMetallic = addUniformDeclaration("roughnessMetallic");
        useAlbedoRoughnessMetallicAO = addUniformDeclaration("useAlbedoRoughnessMetallicAO");
        useNormalTexture = addUniformDeclaration("useNormalTexture");
        transformationIndex = addUniformDeclaration("transformationIndex");
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
        return shaderRepository.gBufferShader;
    }

    @Override
    protected void renderInternal() {
        prepareCall();
        ssboService.bind(ssboRepository.transformationSSBO);

        List<RenderingRequest> requests = renderingRepository.requests;
        int instancedOffset = 0;
        for (int i = 0; i < requests.size(); i++) {
            var request = requests.get(i);
            request.renderIndex = (i + instancedOffset);
            shaderService.bindInt(request.renderIndex, transformationIndex);
            if (request.material != null) {
                bindMaterial(request);
            } else {
                shaderService.bindBoolean(true, fallbackMaterial);
            }
            meshService.bind(request.mesh);
            meshService.setInstanceCount(request.transformationComponents.size());
            meshService.draw();
            if (!request.transformationComponents.isEmpty()) {
                instancedOffset += request.transformationComponents.size() - 1;
            }
        }
    }

    private void bindMaterial(RenderingRequest request) {
        shaderService.bindBoolean(false, fallbackMaterial);

        request.material.anisotropicRotationUniform = anisotropicRotation;
        request.material.anisotropyUniform = anisotropy;
        request.material.clearCoatUniform = clearCoat;
        request.material.sheenUniform = sheen;
        request.material.sheenTintUniform = sheenTint;
        request.material.renderingModeUniform = renderingMode;
        request.material.ssrEnabledUniform = ssrEnabled;
        request.material.parallaxHeightScaleUniform = parallaxHeightScale;
        request.material.parallaxLayersUniform = parallaxLayers;
        request.material.useParallaxUniform = useParallax;

        request.material.albedoColorLocation = albedoColor;
        request.material.roughnessMetallicLocation = roughnessMetallic;
        request.material.useAlbedoRoughnessMetallicAO = useAlbedoRoughnessMetallicAO;
        request.material.useNormalTexture = useNormalTexture;

        request.material.albedoLocation = 3;
        request.material.roughnessLocation = 4;
        request.material.metallicLocation = 5;
        request.material.aoLocation = 6;
        request.material.normalLocation = 7;
        request.material.heightMapLocation = 8;

        materialService.bindMaterial(request.material);
    }

    @Override
    public String getTitle() {
        return "GBuffer generation";
    }
}
