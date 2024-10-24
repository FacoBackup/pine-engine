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

        shaderService.bindFloat(settingsRepository.ssrFalloff, SSRFalloff);
        shaderService.bindFloat(settingsRepository.ssrStepSize, stepSizeSSR);
        shaderService.bindFloat(settingsRepository.sssMaxDistance, maxSSSDistance);
        shaderService.bindFloat(settingsRepository.sssDepthThickness, SSSDepthThickness);
        shaderService.bindFloat(settingsRepository.sssEdgeFalloff, SSSEdgeAttenuation);
        shaderService.bindFloat(settingsRepository.sssDepthDelta, SSSDepthDelta);
        shaderService.bindFloat(settingsRepository.ssaoFalloffDistance, SSAOFalloff);
        shaderService.bindInt(settingsRepository.ssrMaxSteps, maxStepsSSR);
        shaderService.bindInt(settingsRepository.sssMaxSteps, maxStepsSSS);
        shaderService.bindInt(renderingRepository.lightCount, lightCount);

        shaderService.bindSampler2d(fboRepository.gBufferAlbedoSampler, gBufferAlbedoSampler);
        shaderService.bindSampler2d(fboRepository.gBufferNormalSampler, gBufferNormalSampler);
        shaderService.bindSampler2d(fboRepository.gBufferRMAOSampler, gBufferRMAOSampler);
        shaderService.bindSampler2d(fboRepository.gBufferMaterialSampler, gBufferMaterialSampler);
        shaderService.bindSampler2d(fboRepository.gBufferDepthSampler, sceneDepth);

        shaderService.bindSampler2d(fboRepository.brdfSampler, brdfSampler);
        shaderService.bindSampler2d(fboRepository.ssaoBlurredSampler, SSAO);
        shaderService.bindSampler2d(fboRepository.ssgiSampler, SSGI);
        shaderService.bindSampler2d(engine.getTargetFBO().getMainSampler(), previousFrame);
    }

    @Override
    public String getTitle() {
        return "GBuffer Shading";
    }
}
