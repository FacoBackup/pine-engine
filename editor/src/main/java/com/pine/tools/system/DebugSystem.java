package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.DebugShadingModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class DebugSystem extends AbstractSystem {

    @PInject
    public EditorRepository editorSettings;

    private UniformDTO transformationIndex;
    private UniformDTO lightCount;
    private UniformDTO elapsedTime;
    private UniformDTO isDecalPass;
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
    private UniformDTO shadingModel;
    private UniformDTO brdfSampler;
    private UniformDTO SSAO;
    private UniformDTO SSGI;
    private UniformDTO previousFrame;
    private UniformDTO shadowAtlas;
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

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    public void onInitialize() {
        GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
        transformationIndex = toolsResourceRepository.debugShader.addUniformDeclaration("transformationIndex", GLSLType.INT);
        lightCount = toolsResourceRepository.debugShader.addUniformDeclaration("lightCount", GLSLType.INT);
        elapsedTime = toolsResourceRepository.debugShader.addUniformDeclaration("elapsedTime", GLSLType.FLOAT);
        isDecalPass = toolsResourceRepository.debugShader.addUniformDeclaration("isDecalPass", GLSLType.BOOL);
        shadowMapsQuantity = toolsResourceRepository.debugShader.addUniformDeclaration("shadowMapsQuantity", GLSLType.FLOAT);
        shadowMapResolution = toolsResourceRepository.debugShader.addUniformDeclaration("shadowMapResolution", GLSLType.FLOAT);
        SSRFalloff = toolsResourceRepository.debugShader.addUniformDeclaration("SSRFalloff", GLSLType.FLOAT);
        stepSizeSSR = toolsResourceRepository.debugShader.addUniformDeclaration("stepSizeSSR", GLSLType.FLOAT);
        maxSSSDistance = toolsResourceRepository.debugShader.addUniformDeclaration("maxSSSDistance", GLSLType.FLOAT);
        SSSDepthThickness = toolsResourceRepository.debugShader.addUniformDeclaration("SSSDepthThickness", GLSLType.FLOAT);
        SSSEdgeAttenuation = toolsResourceRepository.debugShader.addUniformDeclaration("SSSEdgeAttenuation", GLSLType.FLOAT);
        SSSDepthDelta = toolsResourceRepository.debugShader.addUniformDeclaration("SSSDepthDelta", GLSLType.FLOAT);
        SSAOFalloff = toolsResourceRepository.debugShader.addUniformDeclaration("SSAOFalloff", GLSLType.FLOAT);
        maxStepsSSR = toolsResourceRepository.debugShader.addUniformDeclaration("maxStepsSSR", GLSLType.INT);
        maxStepsSSS = toolsResourceRepository.debugShader.addUniformDeclaration("maxStepsSSS", GLSLType.INT);
        hasAmbientOcclusion = toolsResourceRepository.debugShader.addUniformDeclaration("hasAmbientOcclusion", GLSLType.BOOL);
        shadingModel = toolsResourceRepository.debugShader.addUniformDeclaration("shadingModel", GLSLType.INT);
        brdfSampler = toolsResourceRepository.debugShader.addUniformDeclaration("brdfSampler", GLSLType.SAMPLER_2_D);
        SSAO = toolsResourceRepository.debugShader.addUniformDeclaration("SSAO", GLSLType.SAMPLER_2_D);
        SSGI = toolsResourceRepository.debugShader.addUniformDeclaration("SSGI", GLSLType.SAMPLER_2_D);
        previousFrame = toolsResourceRepository.debugShader.addUniformDeclaration("previousFrame", GLSLType.SAMPLER_2_D);
        shadowAtlas = toolsResourceRepository.debugShader.addUniformDeclaration("shadowAtlas", GLSLType.SAMPLER_2_D);
        shadowCube = toolsResourceRepository.debugShader.addUniformDeclaration("shadowCube", GLSLType.SAMPLER_CUBE);
        ssrEnabled = toolsResourceRepository.debugShader.addUniformDeclaration("ssrEnabled", GLSLType.BOOL);
        renderingMode = toolsResourceRepository.debugShader.addUniformDeclaration("renderingMode", GLSLType.INT);
        anisotropicRotation = toolsResourceRepository.debugShader.addUniformDeclaration("anisotropicRotation", GLSLType.FLOAT);
        anisotropy = toolsResourceRepository.debugShader.addUniformDeclaration("anisotropy", GLSLType.FLOAT);
        clearCoat = toolsResourceRepository.debugShader.addUniformDeclaration("clearCoat", GLSLType.FLOAT);
        sheen = toolsResourceRepository.debugShader.addUniformDeclaration("sheen", GLSLType.FLOAT);
        sheenTint = toolsResourceRepository.debugShader.addUniformDeclaration("sheenTint", GLSLType.FLOAT);
        useAlbedoDecal = toolsResourceRepository.debugShader.addUniformDeclaration("useAlbedoDecal", GLSLType.BOOL);
        useMetallicDecal = toolsResourceRepository.debugShader.addUniformDeclaration("useMetallicDecal", GLSLType.BOOL);
        useRoughnessDecal = toolsResourceRepository.debugShader.addUniformDeclaration("useRoughnessDecal", GLSLType.BOOL);
        useNormalDecal = toolsResourceRepository.debugShader.addUniformDeclaration("useNormalDecal", GLSLType.BOOL);
        useOcclusionDecal = toolsResourceRepository.debugShader.addUniformDeclaration("useOcclusionDecal", GLSLType.BOOL);
    }

    @Override
    protected void renderInternal() {
        GL46.glEnable(GL11.GL_DEPTH_TEST);
        if(editorSettings.debugShadingModel == DebugShadingModel.WIREFRAME){
            meshService.setRenderingMode(RenderingMode.WIREFRAME);
        }else{
            meshService.setRenderingMode(RenderingMode.TRIANGLES);
        }
        ssboService.bind(ssboRepository.transformationSSBO);
        ssboService.bind(ssboRepository.lightMetadataSSBO);
        shaderService.bind(toolsResourceRepository.debugShader);

        intBoolBuffer.put(0, renderingRepository.lightCount);
        shaderService.bindUniform(lightCount, intBoolBuffer);

        floatBuffer.put(0, clockRepository.elapsedTime);
        shaderService.bindUniform(elapsedTime, floatBuffer);

        intBoolBuffer.put(0, 0);
        shaderService.bindUniform(isDecalPass, intBoolBuffer);

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

        intBoolBuffer.put(0, editorSettings.debugShadingModel.getId());
        shaderService.bindUniform(shadingModel, intBoolBuffer);

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
