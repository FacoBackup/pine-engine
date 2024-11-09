package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
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

    @Override
    public void onInitialize() {
        distortionIntensity = addUniformDeclaration("distortionIntensity");
        chromaticAberrationIntensity = addUniformDeclaration("chromaticAberrationIntensity");
        distortionEnabled = addUniformDeclaration("distortionEnabled");
        chromaticAberrationEnabled = addUniformDeclaration("chromaticAberrationEnabled");
        bloomEnabled = addUniformDeclaration("bloomEnabled");
        focusDistanceDOF = addUniformDeclaration("focusDistanceDOF");
        apertureDOF = addUniformDeclaration("apertureDOF");
        focalLengthDOF = addUniformDeclaration("focalLengthDOF");
        samplesDOF = addUniformDeclaration("samplesDOF");
        vignetteEnabled = addUniformDeclaration("vignetteEnabled");
        vignetteStrength = addUniformDeclaration("vignetteStrength");
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return engine.bufferRepository.postProcessingBuffer;
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

//        shaderService.bindSampler2d(, bloomColor);
        shaderService.bindSampler2dDirect(bufferRepository.compositingSampler, 1);

    }

    @Override
    public String getTitle() {
        return "Post processing";
    }
}
