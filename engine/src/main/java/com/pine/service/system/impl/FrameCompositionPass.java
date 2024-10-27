package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;

public class FrameCompositionPass extends AbstractQuadPassPass {
    private UniformDTO inverseFilterTextureSize;
    private UniformDTO useFXAA;
    private UniformDTO filmGrainEnabled;
    private UniformDTO FXAASpanMax;
    private UniformDTO FXAAReduceMin;
    private UniformDTO FXAAReduceMul;
    private UniformDTO filmGrainStrength;
    private UniformDTO currentFrame;
    private UniformDTO filmGrainSeed;
    private final float[] lookUpRandom = new float[2000];
    private int lookUpIndex = 0;

    @Override
    public void onInitialize() {
        inverseFilterTextureSize = addUniformDeclaration("inverseFilterTextureSize");
        useFXAA = addUniformDeclaration("useFXAA");
        filmGrainEnabled = addUniformDeclaration("filmGrainEnabled");
        FXAASpanMax = addUniformDeclaration("FXAASpanMax");
        FXAAReduceMin = addUniformDeclaration("FXAAReduceMin");
        FXAAReduceMul = addUniformDeclaration("FXAAReduceMul");
        filmGrainStrength = addUniformDeclaration("filmGrainStrength");
        currentFrame = addUniformDeclaration("currentFrame");
        filmGrainSeed = addUniformDeclaration("filmGrainSeed");

        for (int i = 0; i < 2000; i++) {
            lookUpRandom[i] = (float) Math.random();
        }
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return engine.getTargetFBO();
    }

    private float lookupNoise() {
        return ++lookUpIndex >= lookUpRandom.length ? lookUpRandom[lookUpIndex = 0] : lookUpRandom[lookUpIndex];
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.frameComposition;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindVec2(runtimeRepository.getInvResolution(), inverseFilterTextureSize);
        shaderService.bindBoolean(settingsRepository.fxaaEnabled, useFXAA);
        shaderService.bindBoolean(cameraRepository.filmGrain, filmGrainEnabled);
        shaderService.bindFloat(settingsRepository.fxaaSpanMax, FXAASpanMax);
        shaderService.bindFloat(settingsRepository.fxaaReduceMin, FXAAReduceMin);
        shaderService.bindFloat(settingsRepository.fxaaReduceMul, FXAAReduceMul);
        shaderService.bindFloat(cameraRepository.filmGrainStrength, filmGrainStrength);
        shaderService.bindSampler2d(fboRepository.postProcessingSampler, currentFrame);
        shaderService.bindFloat(lookupNoise(), filmGrainSeed);
    }

    @Override
    public String getTitle() {
        return "Frame composition";
    }
}
