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
                shaderService.bindBoolean(false, fallbackMaterial);
                bindMaterial(request.material);
            } else {
                shaderService.bindBoolean(true, fallbackMaterial);
            }

            meshService.bind(request.mesh);
            meshService.setInstanceCount(request.transformations.size());
            meshService.draw();
            instancedOffset += request.transformations.size();
        }
    }

    private void bindMaterial(MaterialResourceRef request) {
        request.lastUse = clockRepository.totalTime;

        if (request.albedo != null) {
            shaderService.bindSampler2dDirect(request.albedo, 3);
            request.albedo.lastUse = request.lastUse;
        }
        if (request.roughness != null) {
            shaderService.bindSampler2dDirect(request.roughness, 4);
            request.roughness.lastUse = request.lastUse;
        }
        if (request.metallic != null) {
            shaderService.bindSampler2dDirect(request.metallic, 5);
            request.metallic.lastUse = request.lastUse;
        }
        if (request.ao != null) {
            shaderService.bindSampler2dDirect(request.ao, 6);
            request.ao.lastUse = request.lastUse;
        }
        if (request.normal != null) {
            shaderService.bindSampler2dDirect(request.normal, 7);
            request.normal.lastUse = request.lastUse;
        }
        if (request.heightMap != null) {
            shaderService.bindSampler2dDirect(request.heightMap, 8);
            request.heightMap.lastUse = request.lastUse;
        }
        shaderService.bindFloat(request.anisotropicRotation, anisotropicRotation);
        shaderService.bindFloat(request.anisotropy, anisotropy);
        shaderService.bindFloat(request.clearCoat, clearCoat);
        shaderService.bindFloat(request.sheen, sheen);
        shaderService.bindFloat(request.sheenTint, sheenTint);
        shaderService.bindInt(request.renderingMode.getId(), renderingMode);
        shaderService.bindInt(request.ssrEnabled ? 1 : 0, ssrEnabled);
        shaderService.bindFloat(request.parallaxHeightScale, parallaxHeightScale);
        shaderService.bindInt(request.parallaxLayers, parallaxLayers);
        shaderService.bindBoolean(request.useParallax, useParallax);
    }

    @Override
    public String getTitle() {
        return "GBuffer generation";
    }
}
