package com.pine.service.system.impl;

import com.pine.Loggable;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class FrameCompositionSystem extends AbstractSystem implements Loggable {
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
    private final FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(2);
    private final IntBuffer intBuffer = MemoryUtil.memAllocInt(1);
    private int lookUpIndex = 0;

    @Override
    public void onInitialize() {
        inverseFilterTextureSize = shaderRepository.atmosphereShader.addUniformDeclaration("inverseFilterTextureSize", GLSLType.VEC_2);
        useFXAA = shaderRepository.atmosphereShader.addUniformDeclaration("useFXAA", GLSLType.BOOL);
        filmGrainEnabled = shaderRepository.atmosphereShader.addUniformDeclaration("filmGrainEnabled", GLSLType.BOOL);
        FXAASpanMax = shaderRepository.atmosphereShader.addUniformDeclaration("FXAASpanMax", GLSLType.FLOAT);
        FXAAReduceMin = shaderRepository.atmosphereShader.addUniformDeclaration("FXAAReduceMin", GLSLType.FLOAT);
        FXAAReduceMul = shaderRepository.atmosphereShader.addUniformDeclaration("FXAAReduceMul", GLSLType.FLOAT);
        filmGrainStrength = shaderRepository.atmosphereShader.addUniformDeclaration("filmGrainStrength", GLSLType.FLOAT);
        currentFrame = shaderRepository.atmosphereShader.addUniformDeclaration("currentFrame", GLSLType.SAMPLER_2_D);
        filmGrainSeed = shaderRepository.atmosphereShader.addUniformDeclaration("filmGrainSeed", GLSLType.FLOAT);

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
    protected void renderInternal() {
        shaderService.bind(shaderRepository.frameComposition);

        floatBuffer.put(0, engine.getInvDisplayW());
        floatBuffer.put(1, engine.getInvDisplayH());
        shaderService.bindUniform(inverseFilterTextureSize, floatBuffer);

        intBuffer.put(0, settingsRepository.fxaaEnabled ? 1 : 0);
        shaderService.bindUniform(useFXAA, intBuffer);

        intBuffer.put(0, settingsRepository.filmGrain ? 1 : 0);
        shaderService.bindUniform(filmGrainEnabled, intBuffer);

        floatBuffer.put(0, settingsRepository.fxaaSpanMax);
        shaderService.bindUniform(FXAASpanMax, floatBuffer);

        floatBuffer.put(0, settingsRepository.fxaaReduceMin);
        shaderService.bindUniform(FXAAReduceMin, floatBuffer);

        floatBuffer.put(0, settingsRepository.fxaaReduceMul);
        shaderService.bindUniform(FXAAReduceMul, floatBuffer);

        floatBuffer.put(0, settingsRepository.filmGrainStrength);
        shaderService.bindUniform(filmGrainStrength, floatBuffer);

        shaderService.bindUniform(currentFrame, fboRepository.tempColorWithDepthSampler);

        floatBuffer.put(0, lookupNoise());
        shaderService.bindUniform(filmGrainSeed, floatBuffer);
        meshService.bind(primitiveRepository.quadMesh);
    }
}