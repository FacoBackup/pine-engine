package com.pine.service.system.impl;

import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GBufferShadingPass extends AbstractPass {
    private UniformDTO transformationIndex;
    private UniformDTO lightCount;
    private UniformDTO elapsedTime;
    private UniformDTO shadowMapsQuantity;
    private UniformDTO shadowMapResolution;
    private UniformDTO SSRFalloff;
    private UniformDTO stepSizeSSR;
    private UniformDTO maxSSSDistance;
    private UniformDTO SSSDepthThickness;
    private UniformDTO SSSEdgeAttenuation;
    private UniformDTO SSSDepthDelta;
    private UniformDTO SSAOFalloff;
    private UniformDTO maxStepsSSR;
    private UniformDTO maxStepsSSS;
    private UniformDTO hasAmbientOcclusion;
    private UniformDTO brdfSampler;
    private UniformDTO SSAO;
    private UniformDTO SSGI;
    private UniformDTO previousFrame;
    private UniformDTO shadowAtlas;

    private UniformDTO isDecalPass;
    private UniformDTO shadowCube;
    private UniformDTO ssrEnabled;
    private UniformDTO renderingMode;
    private UniformDTO anisotropicRotation;
    private UniformDTO anisotropy;
    private UniformDTO clearCoat;
    private UniformDTO sheen;
    private UniformDTO sheenTint;
    private UniformDTO useAlbedoDecal;
    private UniformDTO useMetallicDecal;
    private UniformDTO useRoughnessDecal;
    private UniformDTO useNormalDecal;
    private UniformDTO useOcclusionDecal;

    private final IntBuffer intBoolBuffer = MemoryUtil.memAllocInt(1);
    private final FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(1);

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    public void onInitialize() {
        GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
        transformationIndex = shaderRepository.gBufferShading.addUniformDeclaration("transformationIndex", GLSLType.INT);
        lightCount = shaderRepository.gBufferShading.addUniformDeclaration("lightCount", GLSLType.INT);
        elapsedTime = shaderRepository.gBufferShading.addUniformDeclaration("elapsedTime", GLSLType.FLOAT);
        isDecalPass = shaderRepository.gBufferShading.addUniformDeclaration("isDecalPass", GLSLType.BOOL);
        shadowMapsQuantity = shaderRepository.gBufferShading.addUniformDeclaration("shadowMapsQuantity", GLSLType.FLOAT);
        shadowMapResolution = shaderRepository.gBufferShading.addUniformDeclaration("shadowMapResolution", GLSLType.FLOAT);
        SSRFalloff = shaderRepository.gBufferShading.addUniformDeclaration("SSRFalloff", GLSLType.FLOAT);
        stepSizeSSR = shaderRepository.gBufferShading.addUniformDeclaration("stepSizeSSR", GLSLType.FLOAT);
        maxSSSDistance = shaderRepository.gBufferShading.addUniformDeclaration("maxSSSDistance", GLSLType.FLOAT);
        SSSDepthThickness = shaderRepository.gBufferShading.addUniformDeclaration("SSSDepthThickness", GLSLType.FLOAT);
        SSSEdgeAttenuation = shaderRepository.gBufferShading.addUniformDeclaration("SSSEdgeAttenuation", GLSLType.FLOAT);
        SSSDepthDelta = shaderRepository.gBufferShading.addUniformDeclaration("SSSDepthDelta", GLSLType.FLOAT);
        SSAOFalloff = shaderRepository.gBufferShading.addUniformDeclaration("SSAOFalloff", GLSLType.FLOAT);
        maxStepsSSR = shaderRepository.gBufferShading.addUniformDeclaration("maxStepsSSR", GLSLType.INT);
        maxStepsSSS = shaderRepository.gBufferShading.addUniformDeclaration("maxStepsSSS", GLSLType.INT);
        hasAmbientOcclusion = shaderRepository.gBufferShading.addUniformDeclaration("hasAmbientOcclusion", GLSLType.BOOL);
        brdfSampler = shaderRepository.gBufferShading.addUniformDeclaration("brdfSampler", GLSLType.SAMPLER_2_D);
        SSAO = shaderRepository.gBufferShading.addUniformDeclaration("SSAO", GLSLType.SAMPLER_2_D);
        SSGI = shaderRepository.gBufferShading.addUniformDeclaration("SSGI", GLSLType.SAMPLER_2_D);
        previousFrame = shaderRepository.gBufferShading.addUniformDeclaration("previousFrame", GLSLType.SAMPLER_2_D);
        shadowAtlas = shaderRepository.gBufferShading.addUniformDeclaration("shadowAtlas", GLSLType.SAMPLER_2_D);
        shadowCube = shaderRepository.gBufferShading.addUniformDeclaration("shadowCube", GLSLType.SAMPLER_CUBE);
        ssrEnabled = shaderRepository.gBufferShading.addUniformDeclaration("ssrEnabled", GLSLType.BOOL);
        renderingMode = shaderRepository.gBufferShading.addUniformDeclaration("renderingMode", GLSLType.INT);
        anisotropicRotation = shaderRepository.gBufferShading.addUniformDeclaration("anisotropicRotation", GLSLType.FLOAT);
        anisotropy = shaderRepository.gBufferShading.addUniformDeclaration("anisotropy", GLSLType.FLOAT);
        clearCoat = shaderRepository.gBufferShading.addUniformDeclaration("clearCoat", GLSLType.FLOAT);
        sheen = shaderRepository.gBufferShading.addUniformDeclaration("sheen", GLSLType.FLOAT);
        sheenTint = shaderRepository.gBufferShading.addUniformDeclaration("sheenTint", GLSLType.FLOAT);
        useAlbedoDecal = shaderRepository.gBufferShading.addUniformDeclaration("useAlbedoDecal", GLSLType.BOOL);
        useMetallicDecal = shaderRepository.gBufferShading.addUniformDeclaration("useMetallicDecal", GLSLType.BOOL);
        useRoughnessDecal = shaderRepository.gBufferShading.addUniformDeclaration("useRoughnessDecal", GLSLType.BOOL);
        useNormalDecal = shaderRepository.gBufferShading.addUniformDeclaration("useNormalDecal", GLSLType.BOOL);
        useOcclusionDecal = shaderRepository.gBufferShading.addUniformDeclaration("useOcclusionDecal", GLSLType.BOOL);
    }

    @Override
    protected void renderInternal() {
        GL46.glEnable(GL11.GL_DEPTH_TEST);
        if (settingsRepository.wireframe) {
            meshService.setRenderingMode(RenderingMode.WIREFRAME);
        } else {
            meshService.setRenderingMode(RenderingMode.TRIANGLES);
        }
        ssboService.bind(ssboRepository.transformationSSBO);
        ssboService.bind(ssboRepository.lightMetadataSSBO);
        shaderService.bind(shaderRepository.gBufferShading);

        intBoolBuffer.put(0, renderingRepository.lightCount);
        shaderService.bindUniform(lightCount, intBoolBuffer);

        floatBuffer.put(0, clockRepository.elapsedTime);
        shaderService.bindUniform(elapsedTime, floatBuffer);

        intBoolBuffer.put(0, settingsRepository.shadowAtlasQuantity);
        shaderService.bindUniform(shadowMapsQuantity, floatBuffer);

        floatBuffer.put(0, settingsRepository.shadowMapResolution);
        shaderService.bindUniform(shadowMapResolution, floatBuffer);

        floatBuffer.put(0, settingsRepository.ssrFalloff);
        shaderService.bindUniform(SSRFalloff, floatBuffer);

        floatBuffer.put(0, settingsRepository.ssrStepSize);
        shaderService.bindUniform(stepSizeSSR, floatBuffer);

        floatBuffer.put(0, settingsRepository.sssMaxDistance);
        shaderService.bindUniform(maxSSSDistance, floatBuffer);

        floatBuffer.put(0, settingsRepository.sssDepthThickness);
        shaderService.bindUniform(SSSDepthThickness, floatBuffer);

        floatBuffer.put(0, settingsRepository.sssEdgeFalloff);
        shaderService.bindUniform(SSSEdgeAttenuation, floatBuffer);

        floatBuffer.put(0, settingsRepository.sssDepthDelta);
        shaderService.bindUniform(SSSDepthDelta, floatBuffer);

        floatBuffer.put(0, settingsRepository.ssaoFalloffDistance);
        shaderService.bindUniform(SSAOFalloff, floatBuffer);

        intBoolBuffer.put(0, settingsRepository.ssrMaxSteps);
        shaderService.bindUniform(maxStepsSSR, intBoolBuffer);

        intBoolBuffer.put(0, settingsRepository.sssMaxSteps);
        shaderService.bindUniform(maxStepsSSS, intBoolBuffer);

        intBoolBuffer.put(0, 0);
        shaderService.bindUniform(hasAmbientOcclusion, intBoolBuffer);

        shaderService.bindUniform(brdfSampler, fboRepository.brdfSampler);

        shaderService.bindUniform(SSAO, fboRepository.ssaoSampler);

        shaderService.bindUniform(SSGI, fboRepository.ssgiSampler);

        shaderService.bindUniform(previousFrame, fboRepository.auxSampler);

        shaderService.bindUniform(shadowAtlas, fboRepository.shadowsSampler);

        var requests = renderingRepository.requests;
        int instancedOffset = 0;
        for (int i = 0, requestsSize = requests.size(); i < requestsSize; i++) {
            RenderingRequest request = requests.get(i);
            intBoolBuffer.put(0, (i + instancedOffset));
            shaderService.bindUniform(transformationIndex, intBoolBuffer);
            meshService.bind(request.mesh);
            meshService.setInstanceCount(request.transformations.size());
            meshService.draw();
            instancedOffset += request.transformations.size();
        }
    }

    @Override
    public String getTitle() {
        return "GBuffer Shading";
    }
}
