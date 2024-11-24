package com.pine.editor.tools.system;

import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.editor.repository.BrushMode;
import com.pine.editor.repository.EditorMode;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.streaming.ref.TextureResourceRef;
import com.pine.engine.service.system.AbstractPass;
import com.pine.editor.tools.repository.ToolsResourceRepository;
import com.pine.editor.tools.types.ExecutionEnvironment;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import static com.pine.engine.service.resource.shader.ShaderService.COMPUTE_RUNTIME_DATA;

public class PaintGizmoPass extends AbstractPass implements Loggable {
    private static final int LOCAL_SIZE_X = 8;
    private static final int LOCAL_SIZE_Y = 8;
    private static final long TIMEOUT = 250;

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
    private TextureResourceRef lastChangedTexture;
    private long sinceLastChange = 0;

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
        if ((clockRepository.totalTime - sinceLastChange) >= TIMEOUT && lastChangedTexture != null) {
            writeTextureToFile();
            sinceLastChange = clockRepository.totalTime;
            lastChangedTexture = null;
        }
        return runtimeRepository.mousePressed && editorRepository.editorMode != EditorMode.TRANSFORM && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    private void writeTextureToFile() {
        getLogger().warn("Writing modified texture {}", lastChangedTexture.id);
        textureService.writeTexture(importerService.getPathToFile(lastChangedTexture.id, StreamableResourceType.TEXTURE), lastChangedTexture.width, lastChangedTexture.height, lastChangedTexture.texture);
    }

    @Override
    protected void renderInternal() {
        targetTexture = updateTargetTexture();
        if (targetTexture != null) {
            targetTexture.lastUse = clockRepository.totalTime;
            targetTexture.bindForBoth(0);
            dispatch();
            if (lastChangedTexture != null && lastChangedTexture != targetTexture) {
                writeTextureToFile();
            }
            lastChangedTexture = targetTexture;
            sinceLastChange = clockRepository.totalTime;
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
        switch (editorRepository.editorMode){
            case FOLIAGE ->{
                if (editorRepository.foliageForPainting != null && terrainRepository.foliage.containsKey(editorRepository.foliageForPainting)) {
                    shaderService.bindVec3(terrainRepository.foliage.get(editorRepository.foliageForPainting).color, colorForPainting);
                }
            }
            case MATERIAL -> {
                if (editorRepository.materialForPainting != null && terrainRepository.materials.containsKey(editorRepository.materialForPainting)) {
                    shaderService.bindVec3(terrainRepository.materials.get(editorRepository.materialForPainting).color, colorForPainting);
                }
            }
        }


        radiusDensityMode.x = editorRepository.brushRadius;
        radiusDensityMode.y = editorRepository.brushDensity;
        radiusDensityMode.z = editorRepository.brushMode == BrushMode.ADD ? 1 : -1;
        shaderService.bindVec3(radiusDensityMode, radiusDensityUniform);

        xyMouse.x = runtimeRepository.normalizedMouseX;
        xyMouse.y = runtimeRepository.normalizedMouseY;
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
        return "Terrain painting";
    }

    private TextureResourceRef updateTargetTexture() {
        switch (editorRepository.editorMode) {
            case FOLIAGE: {
                if (editorRepository.foliageForPainting != null) {
                    return (TextureResourceRef) streamingService.streamIn(terrainRepository.foliageMask, StreamableResourceType.TEXTURE);
                }
            }
            case TERRAIN: {
                return (TextureResourceRef) streamingService.streamIn(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE);
            }
            case MATERIAL: {
                return (TextureResourceRef) streamingService.streamIn(terrainRepository.materialMask, StreamableResourceType.TEXTURE);
            }
        }
        return null;
    }
}
