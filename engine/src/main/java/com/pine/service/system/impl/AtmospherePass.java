package com.pine.service.system.impl;

import com.pine.injection.PInject;
import com.pine.repository.AtmosphereSettingsRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class AtmospherePass extends AbstractQuadPassPass {

    @PInject
    public AtmosphereSettingsRepository atmosphere;

    private UniformDTO invSkyProjectionMatrix;
    private UniformDTO renderStatic;
    private UniformDTO invViewStatic;
    private UniformDTO type;
    private UniformDTO elapsedTime;
    private UniformDTO rayleighBeta;
    private UniformDTO mieBeta;
    private UniformDTO intensity;
    private UniformDTO atmosphereRadius;
    private UniformDTO planetRadius;
    private UniformDTO rayleighHeight;
    private UniformDTO mieHeight;
    private UniformDTO threshold;
    private UniformDTO samples;

    @Override
    public void onInitialize() {
        renderStatic = shaderRepository.atmosphereShader.addUniformDeclaration("renderStatic", GLSLType.BOOL);
        invViewStatic = shaderRepository.atmosphereShader.addUniformDeclaration("invViewStatic", GLSLType.MAT_4);
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
        shaderService.bindMat4(cameraRepository.invSkyboxProjectionMatrix, invSkyProjectionMatrix);
        shaderService.bindInt(atmosphere.renderingType.getId(), type);
        shaderService.bindFloat(atmosphere.elapsedTime, elapsedTime);
        shaderService.bindVec3(atmosphere.betaRayleigh, rayleighBeta);
        shaderService.bindVec3(atmosphere.betaMie, mieBeta);
        shaderService.bindFloat(atmosphere.intensity, intensity);
        shaderService.bindFloat(atmosphere.atmosphereRadius, atmosphereRadius);
        shaderService.bindFloat(atmosphere.planetRadius, planetRadius);
        shaderService.bindFloat(atmosphere.rayleighHeight, rayleighHeight);
        shaderService.bindFloat(atmosphere.mieHeight, mieHeight);
        shaderService.bindFloat(atmosphere.threshold, threshold);
        shaderService.bindInt(atmosphere.maxSamples, samples);
        shaderService.bindBoolean(false, renderStatic);
    }

    @Override
    public String getTitle() {
        return "Atmosphere rendering";
    }

    public void renderToCubeMap(Matrix4f invViewMatrix, Matrix4f invProjection) {
        shaderService.bind(getShader());
        bindUniforms();
        shaderService.bindMat4(invViewMatrix, invViewStatic);
        shaderService.bindMat4(invProjection, invSkyProjectionMatrix);
        shaderService.bindBoolean(true, renderStatic);
        drawQuad();
    }
}
