package com.pine.service.system.impl;

import com.pine.messaging.Loggable;
import com.pine.repository.rendering.RenderingMode;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL46;

public abstract class AbstractQuadPassPass extends AbstractPass implements Loggable {

    protected void bindUniforms(){}

    @Override
    final protected void renderInternal() {
        bindUniforms();
        drawQuad();
    }

    protected void drawQuad() {
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        meshService.bind(meshRepository.quadMesh);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.draw();
    }
}
