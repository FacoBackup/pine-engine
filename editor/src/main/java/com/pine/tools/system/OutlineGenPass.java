package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.WorldTile;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;


public class OutlineGenPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO renderIndex;
    private UniformDTO modelMatrix;
    private UniformDTO tilesScaleTranslation;
    private UniformDTO textureSize;
    private UniformDTO heightScale;

    @Override
    public void onInitialize() {
        renderIndex = toolsResourceRepository.outlineGenShader.addUniformDeclaration("renderIndex");
        modelMatrix = toolsResourceRepository.outlineGenShader.addUniformDeclaration("modelMatrix");

        textureSize = toolsResourceRepository.outlineTerrainGenShader.addUniformDeclaration("textureSize");
        tilesScaleTranslation = toolsResourceRepository.outlineTerrainGenShader.addUniformDeclaration("tilesScaleTranslation");
        heightScale = toolsResourceRepository.outlineTerrainGenShader.addUniformDeclaration("heightScale");
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

    @Override
    protected void renderInternal() {
        meshService.setInstanceCount(0);
        if (editorRepository.editorMode == EditorMode.TRANSFORM) {
            shaderService.bind(toolsResourceRepository.outlineGenShader);
            for (WorldTile worldTile : worldService.getLoadedTiles()) {
                if (worldTile != null) {
                    for (var entityId : worldTile.getEntities()) {
                        if (!editorRepository.selected.containsKey(entityId)) {
                            continue;
                        }
                        var mesh = world.bagMeshComponent.get(entityId);
                        if (mesh != null && editorRepository.selected.containsKey(mesh.getEntityId()) && mesh.canRender(engineRepository.disableCullingGlobally, world.hiddenEntityMap)) {
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
        } else if(terrainRepository.enabled){
            shaderService.bind(toolsResourceRepository.outlineTerrainGenShader);
            var heightMap = (TextureResourceRef) streamingService.streamIn(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE);
            if (heightMap != null) {
                meshService.renderTerrain(heightMap, textureSize, heightScale, tilesScaleTranslation, null);
            }
        }
    }

    @Override
    public String getTitle() {
        return "Outline Generation";
    }
}
