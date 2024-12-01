package com.pine.engine.service.system.impl;

import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.system.AbstractPass;

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
    protected FBO getTargetFBO() {
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
