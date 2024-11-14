package com.pine.tools.system;

import com.pine.component.MeshComponent;
import com.pine.injection.PInject;
import com.pine.repository.EditorMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.Tile;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector2f;

import java.util.Collection;

import static com.pine.service.grid.HashGrid.TILE_SIZE;


public class OutlineGenPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO renderIndex;
    private UniformDTO modelMatrix;
    private UniformDTO planeSize;
    private UniformDTO heightScale;
    private UniformDTO terrainLocation;
    private final Vector2f terrainLocationV = new Vector2f();

    @Override
    public void onInitialize() {
        renderIndex = toolsResourceRepository.outlineGenShader.addUniformDeclaration("renderIndex");
        modelMatrix = toolsResourceRepository.outlineGenShader.addUniformDeclaration("modelMatrix");

        planeSize = toolsResourceRepository.outlineTerrainGenShader.addUniformDeclaration("planeSize");
        heightScale = toolsResourceRepository.outlineTerrainGenShader.addUniformDeclaration("heightScale");
        terrainLocation = toolsResourceRepository.outlineTerrainGenShader.addUniformDeclaration("terrainLocation");
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

    @Override
    protected void renderInternal() {
        meshService.setInstanceCount(0);
        shaderService.bind(toolsResourceRepository.outlineGenShader);
        for (Tile tile : hashGridService.getLoadedTiles()) {
            if (tile != null) {
                if (editorRepository.editorMode == EditorMode.TRANSFORM) {
                    Collection<MeshComponent> meshes = tile.getWorld().bagMeshComponent.values();
                    for (var mesh : meshes) {
                        if (editorRepository.selected.containsKey(mesh.getEntityId()) && mesh.canRender(settingsRepository.disableCullingGlobally, tile.getWorld().hiddenEntityMap)) {
                            var request = mesh.renderRequest;
                            shaderService.bindInt(request.renderIndex, renderIndex);
                            shaderService.bindMat4(request.modelMatrix, modelMatrix);
                            meshService.bind(request.mesh);
                            meshService.draw();
                        }
                    }
                } else if (editorRepository.selected.containsKey(tile.getId())) {
                    shaderService.bind(toolsResourceRepository.outlineTerrainGenShader);
                    var current = hashGridService.getCurrentTile();
                    renderTileTerrain(current);
                }
            }
        }
    }

    private void renderTileTerrain(Tile current) {
        var heightMap = (TextureResourceRef) streamingService.streamIn(current.terrainHeightMapId, StreamableResourceType.TEXTURE);
        if (heightMap != null) {
            terrainLocationV.x = current.getX() * TILE_SIZE;
            terrainLocationV.y = current.getZ() * TILE_SIZE;

            shaderService.bindInt(heightMap.width, planeSize);
            shaderService.bindFloat(terrainRepository.heightScale, heightScale);
            shaderService.bindVec2(terrainLocationV, terrainLocation);

            shaderService.bindSampler2dDirect(heightMap, 8);

            meshService.renderTerrain(TILE_SIZE);
        }
    }

    @Override
    public String getTitle() {
        return "Outline Generation";
    }
}
