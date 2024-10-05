package com.pine.service.system.impl;

import com.pine.Loggable;
import com.pine.injection.PInject;
import com.pine.repository.AtmosphereSettingsRepository;
import com.pine.repository.rendering.RenderingMode;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public abstract class AbstractQuadPassSystem extends AbstractSystem implements Loggable {

    protected abstract Shader getShader();

    protected abstract void bindUniforms();

    @Override
    final protected void renderInternal() {
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        shaderService.bind(getShader());
        bindUniforms();
        meshService.bind(primitiveRepository.quadMesh);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.draw();
    }
}