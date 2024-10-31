package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.impl.AbstractQuadPassPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;


public class BackgroundPass extends AbstractQuadPassPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO color;

    @Override
    public void onInitialize() {
        color = addUniformDeclaration("color");
    }

    @Override
    protected boolean isRenderable() {
        return editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.backgroundShader;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindVec3(editorRepository.backgroundColor, color);
        shaderService.bindSampler2dDirect(fboRepository.gBufferDepthIndexSampler, 0);
    }

    @Override
    public String getTitle() {
        return "Background";
    }
}
