package com.pine.service.system.impl;

import com.pine.Loggable;
import com.pine.PInject;
import com.pine.component.AtmosphereComponent;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class AtmosphereSystem extends AbstractSystem implements Loggable {

    @PInject
    public AtmosphereComponent atmospheres;

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
        return engine.getTargetFBO();
    }

    @Override
    protected void renderInternal() {
        shaderService.bind(shaderRepository.atmosphereShader);
        cameraRepository.currentCamera.invSkyboxProjectionMatrix.get(invSkyProjectionMatrixB);
        for (var comp : atmospheres.getBag()) {
            typeB.put(0, comp.renderingType.getId());
            elapsedTimeB.put(0, comp.elapsedTime);

            comp.betaRayleigh.get(rayleighBetaB);
            comp.betaMie.get(mieBetaB);

            intensityB.put(0, comp.intensity);
            atmosphereRadiusB.put(0, comp.atmosphereRadius);
            planetRadiusB.put(0, comp.planetRadius);
            rayleighHeightB.put(0, comp.rayleighHeight);
            mieHeightB.put(0, comp.mieHeight);
            thresholdB.put(0, comp.threshold);
            samplesB.put(0, comp.maxSamples);

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

            meshService.bind(primitiveRepository.quadMesh);
        }
        meshService.unbind();
        shaderService.unbind();
    }
}