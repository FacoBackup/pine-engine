package com.pine.tools.repository;

import com.pine.injection.PInject;
import com.pine.repository.RuntimeRepository;
import com.pine.service.module.Initializable;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.voxelization.util.TextureUtil;
import org.lwjgl.opengl.GL46;

public class ToolsResourceRepository implements Initializable {

    @PInject
    public ShaderService shaderService;

    @PInject
    public RuntimeRepository repository;

    public Shader outlineShader;
    public Shader gridShader;
    public Shader outlineBoxGenShader;
    public Shader paintGizmoCompute;
    public Shader paintGizmoRenderingShader;
    public Shader outlineGenShader;
    public Shader iconShader;
    public FrameBufferObject outlineBuffer;
    public int outlineSampler;
    public TextureResourceRef icons;

    @Override
    public void onInitialize() {
        outlineShader = shaderService.create("QUAD.vert", "tool/OUTLINE.frag");
        paintGizmoRenderingShader = shaderService.create("QUAD.vert", "tool/PAINT_GIZMO.frag");
        outlineGenShader = shaderService.create("tool/OUTLINE_GEN.vert", "tool/OUTLINE_GEN.frag");
        outlineBoxGenShader = shaderService.create("tool/OUTLINE_GEN.vert", "tool/OUTLINE_GEN_BOX.frag");
        gridShader = shaderService.create("QUAD.vert", "tool/GRID.frag");
        iconShader = shaderService.create("tool/ICON.vert", "tool/ICON.frag");
        paintGizmoCompute = shaderService.create("compute/PAINT_GIZMO_COMPUTE.glsl");

        outlineBuffer = new FrameBufferObject(repository.getDisplayW(), repository.getDisplayH()).addSampler(0, GL46.GL_R16F, GL46.GL_RED, GL46.GL_FLOAT, false, false);
        outlineSampler = outlineBuffer.getSamplers().getFirst();
        icons = TextureUtil.loadTextureFromResource("/textures/icons.png");
    }
}
