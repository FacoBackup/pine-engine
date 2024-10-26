package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.repository.DebugShadingModel;
import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.util.List;

public class GBufferPass extends AbstractPass implements Loggable {

    private UniformDTO transformationIndex;
    private UniformDTO debugShadingMode;
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

    @Override
    public void onInitialize() {
        debugShadingMode = shaderRepository.gBufferShader.addUniformDeclaration("debugShadingMode", GLSLType.INT);
        transformationIndex = shaderRepository.gBufferShader.addUniformDeclaration("transformationIndex", GLSLType.INT);
        parallaxHeightScale = shaderRepository.gBufferShader.addUniformDeclaration("parallaxHeightScale", GLSLType.FLOAT);
        parallaxLayers = shaderRepository.gBufferShader.addUniformDeclaration("parallaxLayers", GLSLType.INT);
        useParallax = shaderRepository.gBufferShader.addUniformDeclaration("useParallax", GLSLType.BOOL);
        anisotropicRotation = shaderRepository.gBufferShader.addUniformDeclaration("anisotropicRotation", GLSLType.FLOAT);
        anisotropy = shaderRepository.gBufferShader.addUniformDeclaration("anisotropy", GLSLType.FLOAT);
        clearCoat = shaderRepository.gBufferShader.addUniformDeclaration("clearCoat", GLSLType.FLOAT);
        sheen = shaderRepository.gBufferShader.addUniformDeclaration("sheen", GLSLType.FLOAT);
        sheenTint = shaderRepository.gBufferShader.addUniformDeclaration("sheenTint", GLSLType.FLOAT);
        renderingMode = shaderRepository.gBufferShader.addUniformDeclaration("renderingMode", GLSLType.INT);
        ssrEnabled = shaderRepository.gBufferShader.addUniformDeclaration("ssrEnabled", GLSLType.BOOL);
        fallbackMaterial = shaderRepository.gBufferShader.addUniformDeclaration("fallbackMaterial", GLSLType.BOOL);
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.gBuffer;
    }

    @Override
    protected void renderInternal() {
        GL46.glEnable(GL11.GL_DEPTH_TEST);
        GL46.glDisable(GL11.GL_BLEND);
        GL46.glEnable(GL11.GL_CULL_FACE);
        if (settingsRepository.debugShadingModel == DebugShadingModel.WIREFRAME) {
            meshService.setRenderingMode(RenderingMode.WIREFRAME);
            GL46.glDisable(GL11.GL_CULL_FACE);
        } else {
            meshService.setRenderingMode(RenderingMode.TRIANGLES);
        }
        ssboService.bind(ssboRepository.transformationSSBO);

        shaderService.bind(shaderRepository.gBufferShader);

        shaderService.bindInt(settingsRepository.debugShadingModel.getId(), debugShadingMode);
        for (int i = 0; i < renderingRepository.environmentMaps.length; i++) {
            var current = renderingRepository.environmentMaps[i];
            if (current != null) {
                shaderService.bindSamplerCubeDirect(current, i);
                current.lastUse = clockRepository.totalTime;
            }
        }

        List<RenderingRequest> requests = renderingRepository.requests;
        int instancedOffset = 0;
        for (int i = 0; i < requests.size(); i++) {
            var request = requests.get(i);
            shaderService.bindInt((i + instancedOffset), transformationIndex);
            if (request.material != null) {
                bindMaterial(request);
            } else {
                shaderService.bindBoolean(true, fallbackMaterial);
            }

            meshService.bind(request.mesh);
            meshService.setInstanceCount(request.transformations.size());
            meshService.draw();
            instancedOffset += request.transformations.size();
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
