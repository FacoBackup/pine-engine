package com.pine.service.system.impl;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.WorldTile;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MaterialResourceRef;

public class DecalGBufferPass extends AbstractGBufferPass {
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
        return shaderRepository.gBufferDecalShader;
    }

    @Override
    protected void renderInternal() {
        prepareCall();
        meshService.setInstanceCount(0);
        meshService.bind(meshRepository.cubeMesh);
        shaderService.bindSampler2dDirect(bufferRepository.sceneDepthCopySampler, 9);
        for (WorldTile worldTile : worldService.getLoadedTiles()) {
            if (worldTile != null) {
                for (var entityId : worldTile.getEntities()) {
                    var decal = world.bagDecalComponent.get(entityId);
                    if (decal != null && worldService.isEntityVisible(entityId)) {
                        MaterialResourceRef material = (MaterialResourceRef) streamingService.streamIn(decal.material, StreamableResourceType.MATERIAL);
                        if (material != null) {
                            world.entityMap.get(entityId).renderIndex = engineRepository.meshesDrawn;
                            shaderService.bindInt(engineRepository.meshesDrawn, renderIndex);
                            engineRepository.meshesDrawn++;
                            shaderService.bindMat4(world.bagTransformationComponent.get(entityId).modelMatrix, modelMatrix);
                            bindMaterial(material);
                            meshService.draw();
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Decals";
    }
}
