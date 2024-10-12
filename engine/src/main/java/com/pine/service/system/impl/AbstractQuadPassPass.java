package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.repository.rendering.RenderingMode;
import com.pine.service.resource.shader.Shader;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL46;

public abstract class AbstractQuadPassPass extends AbstractPass implements Loggable {

    protected abstract Shader getShader();

    protected abstract void bindUniforms();

    @Override
    final protected void renderInternal() {
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        shaderService.bind(getShader());
        bindUniforms();
        meshService.bind(meshRepository.quadMesh);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.draw();
    }
}
