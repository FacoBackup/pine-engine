package com.pine.engine.service.system.impl;

import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
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
        settings.x = atmosphere.windAmplitude;
        settings.y = atmosphere.windFrequency;
        settings.z = atmosphere.windStrength;
        shaderService.bindVec3(settings, settingsU);
    }

    @Override
    public String getTitle() {
        return "Noise generation";
    }
}
