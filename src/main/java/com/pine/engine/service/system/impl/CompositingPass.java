package com.pine.engine.service.system.impl;

import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;

public class CompositingPass extends AbstractQuadPass {
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
        shaderService.bindSampler2dDirect(bufferRepository.gBufferTargetSampler, 0);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 1);
        shaderService.bindSampler2dDirect(bufferRepository.auxBufferQuaterResSampler, 2);
    }

    @Override
    public String getTitle() {
        return "Compositing pass";
    }
}
