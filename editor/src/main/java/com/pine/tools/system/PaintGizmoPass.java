package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.BrushMode;
import com.pine.repository.EditorMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.grid.WorldTile;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class PaintGizmoPass extends AbstractPass {
    private static final int LOCAL_SIZE_X = 8;
    private static final int LOCAL_SIZE_Y = 8;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO xyMouseUniform;
    private UniformDTO paintMode;
    private UniformDTO heightScale;
    private UniformDTO targetImageSizeUniform;
    private UniformDTO radiusDensityUniform;
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
        radiusDensityUniform = addUniformDeclaration("radiusDensityMode");
        colorForPainting = addUniformDeclaration("colorForPainting");
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.paintGizmoCompute;
    }

    @Override
    protected boolean isRenderable() {
        return runtimeRepository.mousePressed && editorRepository.editorMode != EditorMode.TRANSFORM && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected void renderInternal() {
        switch (editorRepository.editorMode) {
            case FOLIAGE: {
                if (editorRepository.foliageForPainting != null) {
                    targetTexture = (TextureResourceRef) streamingService.streamIn(terrainRepository.foliageMask, StreamableResourceType.TEXTURE);
                }
                break;
            }
            case TERRAIN: {
                targetTexture = (TextureResourceRef) streamingService.streamIn(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE);
                break;
            }
        }

        if (targetTexture != null) {
            targetTexture.bindForBoth(1);
            dispatch();
        }
    }

    private void dispatch() {
        bindUniforms();

        COMPUTE_RUNTIME_DATA.groupX = (bufferRepository.gBuffer.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (bufferRepository.gBuffer.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_BUFFER_UPDATE_BARRIER_BIT | GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        shaderService.dispatch(COMPUTE_RUNTIME_DATA);
    }

    private void bindUniforms() {
        if (editorRepository.foliageForPainting != null && terrainRepository.foliage.containsKey(editorRepository.foliageForPainting)) {
            shaderService.bindVec3(terrainRepository.foliage.get(editorRepository.foliageForPainting).color, colorForPainting);
        }

        radiusDensityMode.x = editorRepository.brushRadius;
        radiusDensityMode.y = editorRepository.brushDensity;
        radiusDensityMode.z = editorRepository.brushMode == BrushMode.ADD ? 1 : -1;
        shaderService.bindVec3(radiusDensityMode, radiusDensityUniform);

        xyMouse.x = (runtimeRepository.mouseX + runtimeRepository.viewportX) / runtimeRepository.viewportW;
        xyMouse.y = (runtimeRepository.viewportH - runtimeRepository.mouseY + runtimeRepository.viewportY) / runtimeRepository.viewportH;
        shaderService.bindVec2(xyMouse, xyMouseUniform);

        targetImageSize.x = targetTexture.width;
        targetImageSize.y = targetTexture.height;
        shaderService.bindVec2(targetImageSize, targetImageSizeUniform);

        shaderService.bindInt(editorRepository.editorMode.index, paintMode);
        shaderService.bindFloat(terrainRepository.heightScale, heightScale);

        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 2);
    }

    @Override
    public String getTitle() {
        return "Paint gizmo";
    }
}
