package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.system.AbstractPass;

public class BRDFGenPass extends AbstractPass {

    private boolean isFirstRun = true;

    @Override
    protected boolean isRenderable() {
        return isFirstRun;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.brdfFBO;
    }

    @Override
    protected void renderInternal() {
        shaderService.bind(shaderRepository.brdfShader);
        meshService.bind(meshRepository.quadMesh);
        meshService.draw();
        isFirstRun = false;
    }

    @Override
    public String getTitle() {
        return "BRDF generation";
    }
}
