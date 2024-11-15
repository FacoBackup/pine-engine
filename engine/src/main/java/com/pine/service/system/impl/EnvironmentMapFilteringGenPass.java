package com.pine.service.system.impl;

import com.pine.repository.rendering.RenderingMode;
import com.pine.service.environment.CubeMapUtil;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.impl.CubeMapFace;
import com.pine.service.system.AbstractPass;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

public class EnvironmentMapFilteringGenPass extends AbstractPass {
    private UniformDTO viewMatrix;
    private UniformDTO projectionMatrix;
    private UniformDTO roughness;
    private int captureFBO;
    private int captureRBO;

    @Override
    public void onInitialize() {
        viewMatrix = addUniformDeclaration("viewMatrix");
        projectionMatrix = addUniformDeclaration("projectionMatrix");
        roughness = addUniformDeclaration("roughness");

        captureFBO = GL46.glGenFramebuffers();
        captureRBO = GL46.glGenRenderbuffers();
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.prefilteredShader;
    }

    @Override
    protected void renderInternal() {
        for (var env : renderingRepository.environmentMaps) {
            if (env != null && env.isLoaded() && !env.hasFiltering) {
                var texture = CubeMapUtil.generateTexture(env.resolution);
                GL46.glGenerateMipmap(GL46.GL_TEXTURE_CUBE_MAP);

                shaderService.bindSamplerCubeDirect(env.texture, 0);
                GL46.glDisable(GL11.GL_CULL_FACE);

                GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, captureFBO);

                for (int mip = 0; mip < engineRepository.probeFiltering; ++mip) {
                    int mipWidth  = (int) (env.resolution * Math.pow(0.5, mip));
                    int mipHeight = (int) (env.resolution * Math.pow(0.5, mip));
                    GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, captureRBO);
                    GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH_COMPONENT24, mipWidth, mipHeight);
                    GL46.glViewport(0, 0, mipWidth, mipHeight);

                    float roughness = (float)mip / (float)(engineRepository.probeFiltering - 1);
                    for (int i = 0; i < CubeMapFace.values().length; ++i) {
                        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0, CubeMapFace.values()[i].getGlFace(), texture, mip);
                        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
                        draw(CubeMapFace.values()[i], roughness);
                    }
                }
                GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, GL11.GL_NONE);
                GL46.glBindFramebuffer(GL46.GL_RENDERBUFFER, GL11.GL_NONE);

                env.hasFiltering = true;
                GL46.glDeleteTextures(env.texture);
                env.texture = texture;
            }
        }
    }

    private void draw(CubeMapFace face, float roughness) {
        shaderService.bindMat4(CubeMapFace.projection, projectionMatrix);
        shaderService.bindFloat(roughness, this.roughness);
        shaderService.bindMat4(CubeMapFace.createViewMatrixForFace(face, new Vector3f(0)), viewMatrix);

        meshService.bind(meshRepository.cubeMesh);
        meshService.setInstanceCount(0);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.draw();
    }

    @Override
    public String getTitle() {
        return "Environment map filtering";
    }
}
