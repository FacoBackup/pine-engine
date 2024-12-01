package com.pine.engine.service.system.impl.tools.outline;

import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.system.impl.AbstractQuadPass;
import com.pine.engine.type.ExecutionEnvironment;


public class OutlineRenderingPass extends AbstractQuadPass {
    @PInject
    public EditorRepository editorRepository;

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
        return shaderRepository.outlineShader;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindSampler2dDirect(bufferRepository.outlineSampler, 0);
        shaderService.bindFloat(editorRepository.outlineWidth, width);
        shaderService.bindVec3(editorRepository.outlineColor, color);
    }

    @Override
    public String getTitle() {
        return "Outline Rendering";
    }
}
