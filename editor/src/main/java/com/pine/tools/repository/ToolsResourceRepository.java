package com.pine.tools.repository;

import com.pine.injection.PInject;
import com.pine.service.module.Initializable;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.ShaderCreationData;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.shader.ShaderCreationData.LOCAL_SHADER;

public class ToolsResourceRepository implements Initializable {

    @PInject
    public ResourceService resources;

    public Shader outlineShader;
    public Shader gridShader;
    public Shader backgroundShader;
    public Shader outlineGenShader;
    public FrameBufferObject outlineBuffer;
    public int outlineSampler;

    @Override
    public void onInitialize() {
        backgroundShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "tool/BACKGROUND.frag"));
        outlineShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "tool/OUTLINE.frag"));
        outlineGenShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "tool/OUTLINE_GEN.vert", LOCAL_SHADER + "tool/OUTLINE_GEN.frag"));
        gridShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "tool/GRID.vert", LOCAL_SHADER + "tool/GRID.frag"));

        outlineBuffer = (FrameBufferObject) resources.addResource(new FBOCreationData(false, false)
                .addSampler(0, GL46.GL_R32F, GL46.GL_RED, GL46.GL_FLOAT, false, false)
                .staticResource());
        outlineSampler = outlineBuffer.getSamplers().getFirst();
    }
}
