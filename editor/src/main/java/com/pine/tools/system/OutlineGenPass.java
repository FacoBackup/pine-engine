package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;


public class OutlineGenPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO model;
    private UniformDTO renderIndex;


    @Override
    public void onInitialize() {
        model = addUniformDeclaration("model");
        renderIndex = addUniformDeclaration("renderIndex");
    }

    @Override
    protected boolean isRenderable() {
        return editorRepository.showOutline && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return toolsResourceRepository.outlineBuffer;
    }

    @Override
    protected boolean shouldClearFBO() {
        return true;
    }

    protected Shader getShader() {
        return toolsResourceRepository.outlineGenShader;
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(ssboRepository.transformationSSBO);

        for (String selected : editorRepository.selected.keySet()) {
            var request = renderingRepository.toBeRendered.get(selected);
            if (request != null) {
                shaderService.bindInt(request.renderIndex, renderIndex);
                meshService.bind(request.mesh);
                meshService.setInstanceCount(request.transformationComponents.size());
                meshService.draw();
            }
        }
    }

    @Override
    public String getTitle() {
        return "Outline Gen";
    }
}
