package com.pine.editor.tools.system.outline;

import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.system.impl.AbstractQuadPass;
import com.pine.editor.tools.repository.ToolsResourceRepository;
import com.pine.editor.tools.types.ExecutionEnvironment;


public class OutlineRenderingPass extends AbstractQuadPass {
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
    protected FBO getTargetFBO() {
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
