package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import org.joml.Vector3f;

public class NoiseGenPass extends AbstractQuadPass {

    private UniformDTO settingsU;
    private final Vector3f settings = new Vector3f();

    @Override
    public void onInitialize() {
        settingsU = addUniformDeclaration("settings");
    }

    @Override
    protected FBO getTargetFBO() {
        return bufferRepository.windNoiseBuffer;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.noiseShader;
    }

    @Override
    protected void bindUniforms() {
        settings.x = terrainRepository.windAmplitude;
        settings.y = terrainRepository.windFrequency;
        settings.z = terrainRepository.windStrength;
        shaderService.bindVec3(settings, settingsU);
    }

    @Override
    public String getTitle() {
        return "Noise generation";
    }
}
