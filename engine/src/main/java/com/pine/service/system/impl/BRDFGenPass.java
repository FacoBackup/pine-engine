package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.system.AbstractPass;

public class BRDFGenPass extends AbstractPass {

    private boolean isFirstRun = true;

    @Override
    protected boolean isRenderable() {
        return isFirstRun;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.brdfShader;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.brdfFBO;
    }

    @Override
    protected void renderInternal() {
        meshService.bind(meshRepository.quadMesh);
        meshService.draw();
        isFirstRun = false;
    }

    @Override
    public String getTitle() {
        return "BRDF generation";
    }
}
