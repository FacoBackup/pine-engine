package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.repository.DebugShadingModel;
import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.material.MaterialStreamableResource;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class GBufferPass extends AbstractPass implements Loggable {

    private final IntBuffer intBuffer = MemoryUtil.memAllocInt(1);
    private final FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(1);

    private UniformDTO transformationIndex;
    private UniformDTO debugShadingMode;
    private UniformDTO albedo;
    private UniformDTO roughness;
    private UniformDTO metallic;
    private UniformDTO ao;
    private UniformDTO normal;
    private UniformDTO heightMap;
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
        albedo = shaderRepository.gBufferShader.addUniformDeclaration("albedo", GLSLType.SAMPLER_2_D);
        roughness = shaderRepository.gBufferShader.addUniformDeclaration("roughness", GLSLType.SAMPLER_2_D);
        metallic = shaderRepository.gBufferShader.addUniformDeclaration("metallic", GLSLType.SAMPLER_2_D);
        ao = shaderRepository.gBufferShader.addUniformDeclaration("ao", GLSLType.SAMPLER_2_D);
        normal = shaderRepository.gBufferShader.addUniformDeclaration("normal", GLSLType.SAMPLER_2_D);
        heightMap = shaderRepository.gBufferShader.addUniformDeclaration("heightMap", GLSLType.SAMPLER_2_D);
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
        if (settingsRepository.debugShadingModel == DebugShadingModel.WIREFRAME) {
            meshService.setRenderingMode(RenderingMode.WIREFRAME);
        } else {
            meshService.setRenderingMode(RenderingMode.TRIANGLES);
        }
        ssboService.bind(ssboRepository.transformationSSBO);
        shaderService.bind(shaderRepository.gBufferShader);

        intBuffer.put(0, settingsRepository.debugShadingModel.getId());
        shaderService.bindUniform(debugShadingMode, intBuffer);

        List<RenderingRequest> requests = renderingRepository.requests;
        int instancedOffset = 0;
        for (int i = 0; i < requests.size(); i++) {
            var request = requests.get(i);
            intBuffer.put(0, (i + instancedOffset));
            shaderService.bindUniform(transformationIndex, intBuffer);
            if(request.material != null) {
                intBuffer.put(0, 0);
                bindMaterial(request.material);
            }else{
                intBuffer.put(0, 1);
            }
            shaderService.bindUniform(fallbackMaterial, intBuffer);

            meshService.bind(request.mesh);
            meshService.setInstanceCount(request.transformations.size());
            meshService.draw();
            instancedOffset += request.transformations.size();
        }
    }

    private void bindMaterial(MaterialStreamableResource request) {
        if(request.albedo != null) {
            shaderService.bindUniform(albedo, request.albedo.texture);
        }

        if(request.roughness != null) {
            shaderService.bindUniform(roughness, request.roughness.texture);
        }

        if(request.metallic != null) {
            shaderService.bindUniform(metallic, request.metallic.texture);
        }

        if(request.ao != null) {
            shaderService.bindUniform(ao, request.ao.texture);
        }

        if(request.normal != null) {
            shaderService.bindUniform(normal, request.normal.texture);
        }

        if(request.heightMap != null) {
            shaderService.bindUniform(heightMap, request.heightMap.texture);
        }

        floatBuffer.put(0, request.anisotropicRotation);
        shaderService.bindUniform(anisotropicRotation, floatBuffer);

        floatBuffer.put(0, request.anisotropy);
        shaderService.bindUniform(anisotropy, floatBuffer);

        floatBuffer.put(0, request.clearCoat);
        shaderService.bindUniform(clearCoat, floatBuffer);

        floatBuffer.put(0, request.sheen);
        shaderService.bindUniform(sheen, floatBuffer);

        floatBuffer.put(0, request.sheenTint);
        shaderService.bindUniform(sheenTint, floatBuffer);

        intBuffer.put(0, request.renderingMode.getId());
        shaderService.bindUniform(renderingMode, intBuffer);

        intBuffer.put(0, request.ssrEnabled ? 1 : 0);
        shaderService.bindUniform(ssrEnabled, intBuffer);

        floatBuffer.put(0, request.parallaxHeightScale);
        shaderService.bindUniform(parallaxHeightScale, floatBuffer);

        intBuffer.put(0, request.parallaxLayers);
        shaderService.bindUniform(parallaxLayers, intBuffer);

        intBuffer.put(0, request.useParallax ? 1 : 0);
        shaderService.bindUniform(useParallax, intBuffer);
    }

    @Override
    public String getTitle() {
        return "GBuffer generation";
    }
}
