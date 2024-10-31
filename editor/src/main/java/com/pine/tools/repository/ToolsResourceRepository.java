package com.pine.tools.repository;

import com.pine.injection.PInject;
import com.pine.repository.RuntimeRepository;
import com.pine.service.module.Initializable;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import org.lwjgl.opengl.GL46;

public class ToolsResourceRepository implements Initializable {

    @PInject
    public ShaderService shaderService;

    @PInject
    public RuntimeRepository repository;

    public Shader outlineShader;
    public Shader gridShader;
    public Shader backgroundShader;
    public Shader outlineGenShader;
    public FrameBufferObject outlineBuffer;
    public int outlineSampler;

    @Override
    public void onInitialize() {
        backgroundShader = shaderService.create("QUAD.vert", "tool/BACKGROUND.frag");
        outlineShader = shaderService.create("QUAD.vert", "tool/OUTLINE.frag");
        outlineGenShader = shaderService.create("tool/OUTLINE_GEN.vert", "tool/OUTLINE_GEN.frag");
        gridShader = shaderService.create("tool/GRID.vert", "tool/GRID.frag");

        outlineBuffer = new FrameBufferObject(repository.getDisplayW(), repository.getDisplayH()).addSampler(0, GL46.GL_R16F, GL46.GL_RED, GL46.GL_FLOAT, false, false);
        outlineSampler = outlineBuffer.getSamplers().getFirst();
    }
}
