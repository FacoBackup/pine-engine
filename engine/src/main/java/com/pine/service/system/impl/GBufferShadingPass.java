package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import org.lwjgl.opengl.GL46;

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
        SSRFalloff = addUniformDeclaration("SSRFalloff");
        stepSizeSSR = addUniformDeclaration("stepSizeSSR");
        maxSSSDistance = addUniformDeclaration("maxSSSDistance");
        SSSDepthThickness = addUniformDeclaration("SSSDepthThickness");
        SSSEdgeAttenuation = addUniformDeclaration("SSSEdgeAttenuation");
        SSSDepthDelta = addUniformDeclaration("SSSDepthDelta");
        SSAOFalloff = addUniformDeclaration("SSAOFalloff");
        maxStepsSSR = addUniformDeclaration("maxStepsSSR");
        maxStepsSSS = addUniformDeclaration("maxStepsSSS");
        lightCount = addUniformDeclaration("lightCount");
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
        shaderService.bindSampler2dDirect(fboRepository.postProcessingSampler, 7);
        shaderService.bindSampler2dDirect(fboRepository.gBufferDepthIndexSampler, 8);
        shaderService.bindSampler2dDirect(fboRepository.gBufferIndirectSampler, 9);
    }


    @Override
    public String getTitle() {
        return "GBuffer Shading";
    }
}
