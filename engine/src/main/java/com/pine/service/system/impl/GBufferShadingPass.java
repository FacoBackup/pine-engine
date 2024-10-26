package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;

public class GBufferShadingPass extends AbstractQuadPassPass {
    private UniformDTO SSRFalloff;
    private UniformDTO stepSizeSSR;
    private UniformDTO maxSSSDistance;
    private UniformDTO SSSDepthThickness;
    private UniformDTO SSSEdgeAttenuation;
    private UniformDTO SSSDepthDelta;
    private UniformDTO SSAOFalloff;
    private UniformDTO maxStepsSSR;
    private UniformDTO maxStepsSSS;
    private UniformDTO lightCount;

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    public void onInitialize() {
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

        shaderService.bindSampler2dDirect(fboRepository.gBufferAlbedoSampler, 0);
        shaderService.bindSampler2dDirect(fboRepository.gBufferNormalSampler, 1);
        shaderService.bindSampler2dDirect(fboRepository.gBufferRMAOSampler, 2);
        shaderService.bindSampler2dDirect(fboRepository.gBufferMaterialSampler, 3);
        shaderService.bindSampler2dDirect(fboRepository.brdfSampler, 4);
        shaderService.bindSampler2dDirect(fboRepository.ssaoBlurredSampler, 5);
        shaderService.bindSampler2dDirect(fboRepository.ssgiSampler, 6);
        shaderService.bindSampler2dDirect(engine.getTargetFBO().getMainSampler(), 7);
        shaderService.bindSampler2dDirect(fboRepository.gBufferDepthIndexSampler, 8);
    }

    @Override
    public String getTitle() {
        return "GBuffer Shading";
    }
}
