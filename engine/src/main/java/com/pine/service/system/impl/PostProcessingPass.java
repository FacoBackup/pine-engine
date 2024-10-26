package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;

public class PostProcessingPass extends AbstractQuadPassPass {
    private UniformDTO distortionIntensity;
    private UniformDTO chromaticAberrationIntensity;
    private UniformDTO distortionEnabled;
    private UniformDTO chromaticAberrationEnabled;
    private UniformDTO bloomEnabled;
    private UniformDTO focusDistanceDOF;
    private UniformDTO apertureDOF;
    private UniformDTO focalLengthDOF;
    private UniformDTO samplesDOF;
    private UniformDTO vignetteEnabled;
    private UniformDTO vignetteStrength;
    private UniformDTO gamma;
    private UniformDTO exposure;
    private UniformDTO sceneColor;
    private UniformDTO bloomColor;

    @Override
    public void onInitialize() {
        distortionIntensity = getShader().addUniformDeclaration("distortionIntensity", GLSLType.FLOAT);
        chromaticAberrationIntensity = getShader().addUniformDeclaration("chromaticAberrationIntensity", GLSLType.FLOAT);
        distortionEnabled = getShader().addUniformDeclaration("distortionEnabled", GLSLType.BOOL);
        chromaticAberrationEnabled = getShader().addUniformDeclaration("chromaticAberrationEnabled", GLSLType.BOOL);
        bloomEnabled = getShader().addUniformDeclaration("bloomEnabled", GLSLType.BOOL);
        focusDistanceDOF = getShader().addUniformDeclaration("focusDistanceDOF", GLSLType.FLOAT);
        apertureDOF = getShader().addUniformDeclaration("apertureDOF", GLSLType.FLOAT);
        focalLengthDOF = getShader().addUniformDeclaration("focalLengthDOF", GLSLType.FLOAT);
        samplesDOF = getShader().addUniformDeclaration("samplesDOF", GLSLType.FLOAT);
        vignetteEnabled = getShader().addUniformDeclaration("vignetteEnabled", GLSLType.BOOL);
        vignetteStrength = getShader().addUniformDeclaration("vignetteStrength", GLSLType.FLOAT);
        gamma = getShader().addUniformDeclaration("gamma", GLSLType.FLOAT);
        exposure = getShader().addUniformDeclaration("exposure", GLSLType.FLOAT);
        bloomColor = getShader().addUniformDeclaration("bloomColor", GLSLType.SAMPLER_2_D);
        sceneColor = getShader().addUniformDeclaration("sceneColor", GLSLType.SAMPLER_2_D);
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return engine.fboRepository.postProcessingBuffer;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.postProcessing;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindFloat(cameraRepository.distortionIntensity, distortionIntensity);
        shaderService.bindFloat(cameraRepository.chromaticAberrationIntensity, chromaticAberrationIntensity);
        shaderService.bindBoolean(cameraRepository.distortionEnabled, distortionEnabled);
        shaderService.bindBoolean(cameraRepository.chromaticAberrationEnabled, chromaticAberrationEnabled);
        shaderService.bindBoolean(cameraRepository.bloomEnabled, bloomEnabled);
        shaderService.bindFloat(cameraRepository.focusDistanceDOF, focusDistanceDOF);
        shaderService.bindFloat(cameraRepository.apertureDOF, apertureDOF);
        shaderService.bindFloat(cameraRepository.focalLengthDOF, focalLengthDOF);
        shaderService.bindFloat(cameraRepository.samplesDOF, samplesDOF);
        shaderService.bindBoolean(cameraRepository.vignetteEnabled, vignetteEnabled);
        shaderService.bindFloat(cameraRepository.vignetteStrength, vignetteStrength);
        shaderService.bindFloat(cameraRepository.gamma, gamma);
        shaderService.bindFloat(cameraRepository.exposure, exposure);

//        shaderService.bindSampler2d(, bloomColor);
        shaderService.bindSampler2d(fboRepository.auxSampler, sceneColor);

    }

    @Override
    public String getTitle() {
        return "Post processing";
    }
}
