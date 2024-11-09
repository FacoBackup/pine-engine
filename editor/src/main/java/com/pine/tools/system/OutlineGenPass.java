package com.pine.tools.system;

import com.pine.component.MeshComponent;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;

import java.util.Collection;


public class OutlineGenPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO renderIndex;
    private UniformDTO modelMatrix;

    @Override
    public void onInitialize() {
        renderIndex = addUniformDeclaration("renderIndex");
        modelMatrix = addUniformDeclaration("modelMatrix");
    }

    @Override
    protected boolean isRenderable() {
        return editorRepository.showOutline && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT && !editorRepository.selected.isEmpty();
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
        meshService.setInstanceCount(0);
        Collection<MeshComponent> meshes = worldRepository.bagMeshComponent.values();
        for (var mesh : meshes) {
            if (mesh.canRender(settingsRepository.disableCullingGlobally, worldRepository.hiddenEntityMap)) {
                var request = mesh.renderRequest;
                if (editorRepository.selected.containsKey(request.entity)) {
                    shaderService.bindInt(request.renderIndex, renderIndex);
                    shaderService.bindMat4(request.modelMatrix, modelMatrix);
                    meshService.bind(request.mesh);
                    meshService.draw();
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Outline Generation";
    }
}
