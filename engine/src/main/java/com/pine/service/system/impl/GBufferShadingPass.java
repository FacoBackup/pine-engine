package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
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
    private UniformDTO sunEnabled;
    private UniformDTO screenSpaceShadows;

    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.gBufferTarget;
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
        sunEnabled = addUniformDeclaration("sunEnabled");
        screenSpaceShadows = addUniformDeclaration("screenSpaceShadows");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferShading;
    }

    @Override
    protected void bindUniforms() {
        ssboService.bind(bufferRepository.lightMetadataSSBO);

        shaderService.bindFloat(engineRepository.ssrFalloff, SSRFalloff);
        shaderService.bindFloat(engineRepository.ssrStepSize, stepSizeSSR);
        shaderService.bindFloat(engineRepository.sssMaxDistance, maxSSSDistance);
        shaderService.bindFloat(engineRepository.sssDepthThickness, SSSDepthThickness);
        shaderService.bindFloat(engineRepository.sssEdgeFalloff, SSSEdgeAttenuation);
        shaderService.bindFloat(engineRepository.sssDepthDelta, SSSDepthDelta);
        shaderService.bindFloat(engineRepository.ssaoFalloffDistance, SSAOFalloff);
        shaderService.bindInt(engineRepository.ssrMaxSteps, maxStepsSSR);
        shaderService.bindInt(engineRepository.sssMaxSteps, maxStepsSSS);
        shaderService.bindInt(renderingRepository.lightCount, lightCount);

        shaderService.bindBoolean(atmosphere.enabled, sunEnabled);
        shaderService.bindBoolean(atmosphere.screenSpaceShadows, screenSpaceShadows);

        shaderService.bindSampler2dDirect(bufferRepository.gBufferAlbedoSampler, 0);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferNormalSampler, 1);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferRMAOSampler, 2);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferMaterialSampler, 3);
        shaderService.bindSampler2dDirect(bufferRepository.brdfSampler, 4);
        shaderService.bindSampler2dDirect(bufferRepository.ssaoBlurredSampler, 5);
//        shaderService.bindSampler2dDirect(fboRepository.ssgiSampler, 6);
        shaderService.bindSampler2dDirect(bufferRepository.postProcessingSampler, 7);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 8);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferIndirectSampler, 9);
    }


    @Override
    public String getTitle() {
        return "GBuffer Shading";
    }
}
