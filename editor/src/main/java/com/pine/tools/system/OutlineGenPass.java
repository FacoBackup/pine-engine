package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;

import java.util.List;


public class OutlineGenPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO model;
    private UniformDTO renderIndex;

    @Override
    public void onInitialize() {
        model = getShader().addUniformDeclaration("model", GLSLType.MAT_4);
        renderIndex = getShader().addUniformDeclaration("renderIndex", GLSLType.INT);
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
        shaderService.bind(getShader());


        List<RenderingRequest> requests = renderingRepository.requests;
        for (int i = 0, requestsSize = requests.size(); i < requestsSize; i++) {
            var request = requests.get(i);
            if(editorRepository.selected.containsKey(request.entity.id)){
                shaderService.bindMat4(request.transformation.globalMatrix, model);
                shaderService.bindInt(i, renderIndex);

                meshService.bind(request.mesh);
                meshService.setInstanceCount(request.transformations.size());
                meshService.draw();
            }
        }
    }

    @Override
    public String getTitle() {
        return "Outline Gen";
    }
}
