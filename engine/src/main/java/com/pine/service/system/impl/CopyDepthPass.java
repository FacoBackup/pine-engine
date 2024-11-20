package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;

public class CopyDepthPass extends AbstractQuadPass {
    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.sceneDepthCopy;
    }

    @Override
    protected boolean shouldClearFBO() {
        return true;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.copyQuadShader;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 0);
    }

    @Override
    public String getTitle() {
        return "Copy depth";
    }
}
