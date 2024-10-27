package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.impl.AbstractQuadPassPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;


public class OutlinePass extends AbstractQuadPassPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO outlineSampler;

    @Override
    public void onInitialize() {
        outlineSampler = addUniformDeclaration("outlineSampler");
    }

    @Override
    protected boolean isRenderable() {
        return editorRepository.showOutline && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.postProcessingBuffer;
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.outlineShader;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindSampler2d(toolsResourceRepository.outlineSampler, outlineSampler);
    }

    @Override
    public String getTitle() {
        return "Outline";
    }
}
