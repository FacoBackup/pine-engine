package com.pine.service.system.impl;

import com.pine.injection.PInject;
import com.pine.repository.AtmosphereSettingsRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class AtmospherePass extends AbstractQuadPassPass {

    @PInject
    public AtmosphereSettingsRepository atmosphere;

    private UniformDTO invSkyProjectionMatrix;
    private FloatBuffer invSkyProjectionMatrixB = MemoryUtil.memAllocFloat(16);

    private UniformDTO type;
    private IntBuffer typeB = MemoryUtil.memAllocInt(1);

    private UniformDTO elapsedTime;
    private FloatBuffer elapsedTimeB = MemoryUtil.memAllocFloat(1);

    private UniformDTO rayleighBeta;
    private FloatBuffer rayleighBetaB = MemoryUtil.memAllocFloat(3);

    private UniformDTO mieBeta;
    private FloatBuffer mieBetaB = MemoryUtil.memAllocFloat(3);

    private UniformDTO intensity;
    private FloatBuffer intensityB = MemoryUtil.memAllocFloat(1);

    private UniformDTO atmosphereRadius;
    private FloatBuffer atmosphereRadiusB = MemoryUtil.memAllocFloat(1);

    private UniformDTO planetRadius;
    private FloatBuffer planetRadiusB = MemoryUtil.memAllocFloat(1);

    private UniformDTO rayleighHeight;
    private FloatBuffer rayleighHeightB = MemoryUtil.memAllocFloat(1);

    private UniformDTO mieHeight;
    private FloatBuffer mieHeightB = MemoryUtil.memAllocFloat(1);

    private UniformDTO threshold;
    private FloatBuffer thresholdB = MemoryUtil.memAllocFloat(1);

    private UniformDTO samples;
    private IntBuffer samplesB = MemoryUtil.memAllocInt(1);

    @Override
    public void onInitialize() {
        invSkyProjectionMatrix = shaderRepository.atmosphereShader.addUniformDeclaration("invSkyProjectionMatrix", GLSLType.MAT_4);
        type = shaderRepository.atmosphereShader.addUniformDeclaration("type", GLSLType.INT);
        elapsedTime = shaderRepository.atmosphereShader.addUniformDeclaration("elapsedTime", GLSLType.FLOAT);
        rayleighBeta = shaderRepository.atmosphereShader.addUniformDeclaration("rayleighBeta", GLSLType.VEC_3);
        mieBeta = shaderRepository.atmosphereShader.addUniformDeclaration("mieBeta", GLSLType.VEC_3);
        intensity = shaderRepository.atmosphereShader.addUniformDeclaration("intensity", GLSLType.FLOAT);
        atmosphereRadius = shaderRepository.atmosphereShader.addUniformDeclaration("atmosphereRadius", GLSLType.FLOAT);
        planetRadius = shaderRepository.atmosphereShader.addUniformDeclaration("planetRadius", GLSLType.FLOAT);
        rayleighHeight = shaderRepository.atmosphereShader.addUniformDeclaration("rayleighHeight", GLSLType.FLOAT);
        mieHeight = shaderRepository.atmosphereShader.addUniformDeclaration("mieHeight", GLSLType.FLOAT);
        threshold = shaderRepository.atmosphereShader.addUniformDeclaration("threshold", GLSLType.FLOAT);
        samples = shaderRepository.atmosphereShader.addUniformDeclaration("samples", GLSLType.INT);
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    protected boolean shouldClearFBO() {
        return true;
    }

    @Override
    protected boolean isRenderable() {
        return atmosphere.enabled;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.atmosphereShader;
    }

    @Override
    protected void bindUniforms() {
        cameraRepository.invSkyboxProjectionMatrix.get(invSkyProjectionMatrixB);

        typeB.put(0, atmosphere.renderingType.getId());
        elapsedTimeB.put(0, atmosphere.elapsedTime);

        atmosphere.betaRayleigh.get(rayleighBetaB);
        atmosphere.betaMie.get(mieBetaB);

        intensityB.put(0, atmosphere.intensity);
        atmosphereRadiusB.put(0, atmosphere.atmosphereRadius);
        planetRadiusB.put(0, atmosphere.planetRadius);
        rayleighHeightB.put(0, atmosphere.rayleighHeight);
        mieHeightB.put(0, atmosphere.mieHeight);
        thresholdB.put(0, atmosphere.threshold);
        samplesB.put(0, atmosphere.maxSamples);

        shaderService.bindUniform(invSkyProjectionMatrix, invSkyProjectionMatrixB);
        shaderService.bindUniform(type, typeB);
        shaderService.bindUniform(elapsedTime, elapsedTimeB);
        shaderService.bindUniform(rayleighBeta, rayleighBetaB);
        shaderService.bindUniform(mieBeta, mieBetaB);
        shaderService.bindUniform(intensity, intensityB);
        shaderService.bindUniform(atmosphereRadius, atmosphereRadiusB);
        shaderService.bindUniform(planetRadius, planetRadiusB);
        shaderService.bindUniform(rayleighHeight, rayleighHeightB);
        shaderService.bindUniform(mieHeight, mieHeightB);
        shaderService.bindUniform(threshold, thresholdB);
        shaderService.bindUniform(samples, samplesB);
    }

    @Override
    public String getTitle() {
        return "Atmosphere rendering";
    }
}
