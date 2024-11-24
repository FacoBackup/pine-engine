package com.pine.engine.service.system.impl.tools.outline;

import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorMode;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.system.AbstractPass;
import com.pine.engine.service.world.WorldTile;
import com.pine.engine.type.ExecutionEnvironment;
import org.lwjgl.opengl.GL46;


public class BoxOutlineGenPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    private UniformDTO renderIndex;
    private UniformDTO modelMatrix;

    @Override
    public void onInitialize() {
        renderIndex = addUniformDeclaration("renderIndex");
        modelMatrix = addUniformDeclaration("modelMatrix");

    }

    @Override
    protected boolean isRenderable() {
        return !editorRepository.selected.isEmpty() && editorRepository.showOutline && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.outlineBoxGenShader;
    }

    @Override
    protected FBO getTargetFBO() {
        return bufferRepository.outlineBuffer;
    }

    @Override
    protected void renderInternal() {
        GL46.glDisable(GL46.GL_CULL_FACE);
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        meshService.setInstanceCount(0);
        for (WorldTile worldTile : worldService.getLoadedTiles()) {
            if (worldTile != null) {
                if (editorRepository.editorMode == EditorMode.TRANSFORM) {
                    for (var entityId : worldTile.getEntities()) {
                        if (!editorRepository.selected.containsKey(entityId)) {
                            continue;
                        }
                        if (world.bagDecalComponent.containsKey(entityId)) {
                            var transform = world.bagTransformationComponent.get(entityId);
                            var entity = world.entityMap.get(entityId);
                            shaderService.bindInt(entity.renderIndex, renderIndex);
                            shaderService.bindMat4(transform.modelMatrix, modelMatrix);
                            meshService.bind(meshRepository.cubeMesh);
                            meshService.draw();
                        }
                    }
                }
            }
        }
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    @Override
    public String getTitle() {
        return "Volume Outline Generation";
    }
}
