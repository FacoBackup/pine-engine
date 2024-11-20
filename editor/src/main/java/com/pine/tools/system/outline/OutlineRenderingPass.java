package com.pine.tools.system.outline;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.impl.AbstractQuadPassPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;


public class OutlineRenderingPass extends AbstractQuadPassPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO color;
    private UniformDTO width;

    @Override
    public void onInitialize() {
        color = addUniformDeclaration("color");
        width = addUniformDeclaration("width");
    }

    @Override
    protected boolean isRenderable() {
        return editorRepository.showOutline && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT && !editorRepository.selected.isEmpty();
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.postProcessingBuffer;
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.outlineShader;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindSampler2dDirect(toolsResourceRepository.outlineSampler, 0);
        shaderService.bindFloat(editorRepository.outlineWidth, width);
        shaderService.bindVec3(editorRepository.outlineColor, color);
    }

    @Override
    public String getTitle() {
        return "Outline Rendering";
    }
}
