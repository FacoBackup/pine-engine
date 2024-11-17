package com.pine.service.system.impl;

import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.grid.WorldTile;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;

public class PrimitiveGBufferPass extends AbstractGBufferPass {
    private UniformDTO renderIndex;

    private UniformDTO modelMatrix;

    @Override
    public void onInitialize() {
        super.onInitialize();
        modelMatrix = addUniformDeclaration("modelMatrix");
        renderIndex = addUniformDeclaration("renderIndex");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferShader;
    }

    @Override
    protected void renderInternal() {
        prepareCall();
        meshService.setInstanceCount(0);

        engineRepository.meshesDrawn = 0;
        for (WorldTile worldTile : worldService.getLoadedTiles()) {
            if (worldTile != null) {
                for (var entityId : worldTile.getEntities()) {
                    var mesh = world.bagMeshComponent.get(entityId);
                    if (mesh != null && mesh.canRender(engineRepository.disableCullingGlobally, world.hiddenEntityMap)) {
                        var request = mesh.renderRequest;
                        var entity = world.entityMap.get(entityId);
                        entity.renderIndex = engineRepository.meshesDrawn;
                        engineRepository.meshesDrawn++;
                        // TODO - SINGLE BUFFER FOR EVERY MESH ATTRIBUTE (Material ID, Model Matrix, Transformation Index); BIND BUFFER INSTEAD OF INDIVIDUAL BINDS
                        shaderService.bindInt(entity.renderIndex, renderIndex);
                        shaderService.bindMat4(request.modelMatrix, modelMatrix);
                        bindMaterial(request.material);
                        meshService.bind(request.mesh);
                        meshService.draw();
                    }
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "GBuffer generation";
    }
}
