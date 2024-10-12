package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GBufferShadingPass extends AbstractQuadPassPass {
    private final IntBuffer intBoolBuffer = MemoryUtil.memAllocInt(1);
    private final FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(1);

    private UniformDTO gBufferAlbedoSampler;
    private UniformDTO gBufferNormalSampler;
    private UniformDTO gBufferRMAOSampler;
    private UniformDTO gBufferMaterialSampler;
    private UniformDTO brdfSampler;
    private UniformDTO SSAO;
    private UniformDTO SSGI;
    private UniformDTO previousFrame;
    private UniformDTO SSRFalloff;
    private UniformDTO stepSizeSSR;
    private UniformDTO maxSSSDistance;
    private UniformDTO SSSDepthThickness;
    private UniformDTO SSSEdgeAttenuation;
    private UniformDTO SSSDepthDelta;
    private UniformDTO SSAOFalloff;
    private UniformDTO maxStepsSSR;
    private UniformDTO maxStepsSSS;
    private UniformDTO sceneDepth;
    private UniformDTO lightCount;

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    public void onInitialize() {
        sceneDepth = shaderRepository.gBufferShading.addUniformDeclaration("sceneDepth", GLSLType.SAMPLER_2_D);
        gBufferAlbedoSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferAlbedoSampler", GLSLType.SAMPLER_2_D);
        gBufferNormalSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferNormalSampler", GLSLType.SAMPLER_2_D);
        gBufferRMAOSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferRMAOSampler", GLSLType.SAMPLER_2_D);
        gBufferMaterialSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferMaterialSampler", GLSLType.SAMPLER_2_D);
        brdfSampler = shaderRepository.gBufferShading.addUniformDeclaration("brdfSampler", GLSLType.SAMPLER_2_D);
        SSAO = shaderRepository.gBufferShading.addUniformDeclaration("SSAO", GLSLType.SAMPLER_2_D);
        SSGI = shaderRepository.gBufferShading.addUniformDeclaration("SSGI", GLSLType.SAMPLER_2_D);
        previousFrame = shaderRepository.gBufferShading.addUniformDeclaration("previousFrame", GLSLType.SAMPLER_2_D);

        SSRFalloff = shaderRepository.gBufferShading.addUniformDeclaration("SSRFalloff", GLSLType.FLOAT);
        stepSizeSSR = shaderRepository.gBufferShading.addUniformDeclaration("stepSizeSSR", GLSLType.FLOAT);
        maxSSSDistance = shaderRepository.gBufferShading.addUniformDeclaration("maxSSSDistance", GLSLType.FLOAT);
        SSSDepthThickness = shaderRepository.gBufferShading.addUniformDeclaration("SSSDepthThickness", GLSLType.FLOAT);
        SSSEdgeAttenuation = shaderRepository.gBufferShading.addUniformDeclaration("SSSEdgeAttenuation", GLSLType.FLOAT);
        SSSDepthDelta = shaderRepository.gBufferShading.addUniformDeclaration("SSSDepthDelta", GLSLType.FLOAT);
        SSAOFalloff = shaderRepository.gBufferShading.addUniformDeclaration("SSAOFalloff", GLSLType.FLOAT);
        maxStepsSSR = shaderRepository.gBufferShading.addUniformDeclaration("maxStepsSSR", GLSLType.INT);
        maxStepsSSS = shaderRepository.gBufferShading.addUniformDeclaration("maxStepsSSS", GLSLType.INT);
        lightCount = shaderRepository.gBufferShading.addUniformDeclaration("lightCount", GLSLType.INT);
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferShading;
    }

    @Override
    protected void bindUniforms() {
        ssboService.bind(ssboRepository.lightMetadataSSBO);

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
        intBoolBuffer.put(0, renderingRepository.lightCount);
        shaderService.bindUniform(lightCount, intBoolBuffer);

        shaderService.bindUniform(gBufferAlbedoSampler, fboRepository.gBufferAlbedoSampler);
        shaderService.bindUniform(gBufferNormalSampler, fboRepository.gBufferNormalSampler);
        shaderService.bindUniform(gBufferRMAOSampler, fboRepository.gBufferRMAOSampler);
        shaderService.bindUniform(gBufferMaterialSampler, fboRepository.gBufferMaterialSampler);
        shaderService.bindUniform(sceneDepth, fboRepository.gBufferDepthSampler);

        shaderService.bindUniform(brdfSampler, fboRepository.brdfSampler);
        shaderService.bindUniform(SSAO, fboRepository.ssaoBlurredSampler);
        shaderService.bindUniform(SSGI, fboRepository.ssgiSampler);
        shaderService.bindUniform(previousFrame, engine.getTargetFBO().getMainSampler());
    }

    @Override
    public String getTitle() {
        return "GBuffer Shading";
    }
}
