package com.pine.service.system.impl;

import com.pine.repository.DebugShadingModel;
import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GBufferShadingPass extends AbstractQuadPassPass {
    private final IntBuffer intBoolBuffer = MemoryUtil.memAllocInt(1);
    private final FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(1);

    private UniformDTO gBufferAlbedoSampler;
    private UniformDTO gBufferNormalSampler;
    private UniformDTO gBufferRMAOSampler;
    private UniformDTO gBufferMaterialSampler;
    private UniformDTO gBufferDepthSampler;

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    public void onInitialize() {
        gBufferAlbedoSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferAlbedoSampler", GLSLType.SAMPLER_2_D);
        gBufferNormalSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferNormalSampler", GLSLType.SAMPLER_2_D);
        gBufferRMAOSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferRMAOSampler", GLSLType.SAMPLER_2_D);
        gBufferMaterialSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferMaterialSampler", GLSLType.SAMPLER_2_D);
        gBufferDepthSampler = shaderRepository.gBufferShading.addUniformDeclaration("gBufferDepthSampler", GLSLType.SAMPLER_2_D);
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferShading;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindUniform(gBufferAlbedoSampler, fboRepository.gBufferAlbedoSampler);
        shaderService.bindUniform(gBufferNormalSampler, fboRepository.gBufferNormalSampler);
        shaderService.bindUniform(gBufferRMAOSampler, fboRepository.gBufferRMAOSampler);
        shaderService.bindUniform(gBufferMaterialSampler, fboRepository.gBufferMaterialSampler);
        shaderService.bindUniform(gBufferDepthSampler, fboRepository.gBufferDepthSampler);
    }

    @Override
    public String getTitle() {
        return "GBuffer Shading";
    }
}
