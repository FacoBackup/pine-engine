package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.BrushMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.GizmoType;
import com.pine.repository.PaintingType;
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
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import static com.pine.service.grid.HashGrid.TILE_SIZE;
import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class PaintGizmoPass extends AbstractPass {
    private static final int LOCAL_SIZE_X = 1;
    private static final int LOCAL_SIZE_Y = 1;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private final Vector3f terrainLocation = new Vector3f();
    private UniformDTO xyMouseUniform;
    private UniformDTO paintMode;
    private UniformDTO heightScale;
    private UniformDTO targetImageSizeUniform;
    private UniformDTO radiusDensityUniform;
    private UniformDTO terrainLocationU;
    private UniformDTO colorForPainting;
    private final Vector2f xyMouse = new Vector2f();
    private final Vector3f radiusDensityMode = new Vector3f();
    private final Vector2f targetImageSize = new Vector2f();
    private TextureResourceRef targetTexture;

    @Override
    public void onInitialize() {
        paintMode = addUniformDeclaration("paintMode");
        heightScale = addUniformDeclaration("heightScale");
        targetImageSizeUniform = addUniformDeclaration("targetImageSize");
        xyMouseUniform = addUniformDeclaration("xyMouse");
        terrainLocationU = addUniformDeclaration("terrainLocation");
        radiusDensityUniform = addUniformDeclaration("radiusDensityMode");
        colorForPainting = addUniformDeclaration("colorForPainting");
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.paintGizmoCompute;
    }

    @Override
    protected boolean isRenderable() {
        return runtimeRepository.mousePressed && editorRepository.gizmoType == GizmoType.PAINT && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected void renderInternal() {
        // TODO - FIX PAINTING APPLYING TO ALL TILES
        for (var tile : hashGridService.getLoadedTiles()) {
            if (tile != null && tile.isTerrainPresent) {
                switch (editorRepository.paintingType) {
                    case FOLIAGE: {
                        if (editorRepository.foliageForPainting != null) {
                            targetTexture = (TextureResourceRef) streamingService.streamIn(tile.terrainFoliageId, StreamableResourceType.TEXTURE);
                        }
                        break;
                    }
                    case TERRAIN: {
                        targetTexture = (TextureResourceRef) streamingService.streamIn(tile.terrainHeightMapId, StreamableResourceType.TEXTURE);
                        break;
                    }
                }

                if (targetTexture != null) {
                    targetTexture.bindForBoth(1);
                    dispatch(tile);
                }
            }
        }
    }

    private void dispatch(Tile tile) {
        updateUniforms();
        bindUniforms(tile);

        COMPUTE_RUNTIME_DATA.groupX = (bufferRepository.gBuffer.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (bufferRepository.gBuffer.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_BUFFER_UPDATE_BARRIER_BIT | GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        shaderService.dispatch(COMPUTE_RUNTIME_DATA);
    }

    private void updateUniforms() {
        radiusDensityMode.x = editorRepository.brushRadius;
        radiusDensityMode.y = editorRepository.brushDensity;
        radiusDensityMode.z = editorRepository.brushMode == BrushMode.ADD ? 1 : -1;

        xyMouse.x = (runtimeRepository.mouseX + runtimeRepository.viewportX) / runtimeRepository.viewportW;
        xyMouse.y = (runtimeRepository.viewportH - runtimeRepository.mouseY + runtimeRepository.viewportY) / runtimeRepository.viewportH;

        targetImageSize.x = targetTexture.width;
        targetImageSize.y = targetTexture.height;
    }

    private void bindUniforms(Tile tile) {
        if (editorRepository.foliageForPainting != null && terrainRepository.foliage.containsKey(editorRepository.foliageForPainting)) {
            shaderService.bindVec3(terrainRepository.foliage.get(editorRepository.foliageForPainting).color, colorForPainting);
        }

        terrainLocation.x = tile.getX();
        terrainLocation.y = tile.getZ();
        terrainLocation.z = TILE_SIZE;

        shaderService.bindVec3(terrainLocation, terrainLocationU);
        shaderService.bindVec3(radiusDensityMode, radiusDensityUniform);
        shaderService.bindVec2(xyMouse, xyMouseUniform);
        shaderService.bindVec2(targetImageSize, targetImageSizeUniform);
        shaderService.bindInt(editorRepository.paintingType.id, paintMode);
        shaderService.bindFloat(terrainRepository.heightScale, heightScale);

        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 2);
    }

    @Override
    public String getTitle() {
        return "Paint gizmo";
    }
}
