package com.pine.editor.tools.system.outline;

import com.pine.editor.tools.repository.ToolsResourceRepository;
import com.pine.editor.tools.types.ExecutionEnvironment;
import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorMode;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.service.world.WorldTile;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.system.AbstractPass;


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
    protected Shader getShader() {
        return toolsResourceRepository.outlineGenShader;
    }

    @Override
    protected boolean isRenderable() {
        return !editorRepository.selected.isEmpty() && editorRepository.editorMode == EditorMode.TRANSFORM && editorRepository.showOutline && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected FBO getTargetFBO() {
        return toolsResourceRepository.outlineBuffer;
    }

    @Override
    protected boolean shouldClearFBO() {
        return true;
    }

    @Override
    protected void renderInternal() {
        meshService.setInstanceCount(0);
        for (WorldTile worldTile : worldService.getLoadedTiles()) {
            if (worldTile != null) {
                for (var entityId : worldTile.getEntities()) {
                    if (!editorRepository.selected.containsKey(entityId)) {
                        continue;
                    }
                    var mesh = world.bagMeshComponent.get(entityId);
                    if (worldService.isMeshReady(mesh)) {
                        var request = mesh.renderRequest;
                        var entity = world.entityMap.get(entityId);
                        shaderService.bindInt(entity.renderIndex, renderIndex);
                        shaderService.bindMat4(request.modelMatrix, modelMatrix);
                        meshService.bind(request.mesh);
                        meshService.draw();
                    }
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Selection Outline Generation";
    }
}
