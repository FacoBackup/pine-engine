package com.pine.service.system.impl;

import com.pine.repository.rendering.RenderingMode;
import com.pine.service.environment.CubeMapGenerator;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.impl.CubeMapFace;
import com.pine.service.system.AbstractPass;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

public class IrradianceGenPass extends AbstractPass {
    private static final int RES = 32;
    private UniformDTO viewMatrix;
    private UniformDTO projectionMatrix;
    private int captureFBO;
    private int captureRBO;

    @Override
    public void onInitialize() {
        viewMatrix = addUniformDeclaration("viewMatrix");
        projectionMatrix = addUniformDeclaration("projectionMatrix");

        captureFBO = GL46.glGenFramebuffers();
        captureRBO = GL46.glGenRenderbuffers();
        bindBuffers();
        GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH_COMPONENT24, RES, RES);
        unbindBuffers();
    }

    private void bindBuffers() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, captureFBO);
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, captureRBO);
    }

    private void unbindBuffers() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, GL46.GL_NONE);
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, GL46.GL_NONE);
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.irradianceShader;
    }

    @Override
    protected void renderInternal() {
        for (var env : renderingRepository.environmentMaps) {
            if (env != null && env.isLoaded() && !env.hasIrradianceGenerated) {
                env.irradiance = CubeMapGenerator.generateTexture(RES);
                shaderService.bindSamplerCubeDirect(env, 0);
                GL46.glDisable(GL11.GL_CULL_FACE);

                GL46.glViewport(0, 0, RES, RES);
                bindBuffers();
                for (int i = 0; i < CubeMapFace.values().length; ++i) {
                    var face = CubeMapFace.values()[i];
                    GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0, face.getGlFace(), env.irradiance, 0);
                    GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
                    draw(CubeMapFace.values()[i]);
                }
                env.hasIrradianceGenerated = true;
                unbindBuffers();
            }
        }
    }

    private void draw(CubeMapFace face) {
        shaderService.bindMat4(CubeMapFace.projection, projectionMatrix);
        shaderService.bindMat4(CubeMapFace.createViewMatrixForFace(face, new Vector3f(0)), viewMatrix);

        meshService.bind(meshRepository.cubeMesh);
        meshService.setInstanceCount(0);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.draw();
    }

    @Override
    public String getTitle() {
        return "Irradiance generation";
    }
}
