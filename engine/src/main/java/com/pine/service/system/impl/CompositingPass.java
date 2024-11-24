package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;

public class CompositingPass extends AbstractQuadPass {

    private UniformDTO backgroundColor;

    @Override
    public void onInitialize() {
        backgroundColor = addUniformDeclaration("backgroundColor");
    }

    @Override
    protected FBO getTargetFBO() {
        return bufferRepository.compositingBuffer;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.compositingShader;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindVec3(engineRepository.backgroundColor, backgroundColor);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferTargetSampler, 0);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 1);
        shaderService.bindSampler2dDirect(bufferRepository.auxBufferQuaterResSampler, 2);
    }

    @Override
    public String getTitle() {
        return "Compositing pass";
    }
}
