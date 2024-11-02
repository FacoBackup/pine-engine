package com.pine.service.system.impl;

import com.pine.injection.PInject;
import com.pine.repository.AtmosphereSettingsRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import org.joml.Matrix4f;

public class AtmospherePass extends AbstractQuadPassPass {

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
        renderStatic = addUniformDeclaration("renderStatic");
        invViewStatic = addUniformDeclaration("invViewStatic");
        invSkyProjectionMatrix = addUniformDeclaration("invSkyProjectionMatrix");
        type = addUniformDeclaration("type");
        elapsedTime = addUniformDeclaration("elapsedTime");
        rayleighBeta = addUniformDeclaration("rayleighBeta");
        mieBeta = addUniformDeclaration("mieBeta");
        intensity = addUniformDeclaration("intensity");
        atmosphereRadius = addUniformDeclaration("atmosphereRadius");
        planetRadius = addUniformDeclaration("planetRadius");
        rayleighHeight = addUniformDeclaration("rayleighHeight");
        mieHeight = addUniformDeclaration("mieHeight");
        threshold = addUniformDeclaration("threshold");
        samples = addUniformDeclaration("samples");
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
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
        shaderService.bindSampler2dDirect(fboRepository.gBufferDepthIndexSampler, 0);
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
