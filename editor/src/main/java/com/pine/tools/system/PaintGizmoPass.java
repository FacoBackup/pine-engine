package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.BrushMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.GizmoType;
import com.pine.repository.streaming.StreamableResourceType;
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

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class PaintGizmoPass extends AbstractPass {
    private static final int LOCAL_SIZE_X = 4;
    private static final int LOCAL_SIZE_Y = 4;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;
    private UniformDTO xyDownUniform;
    private UniformDTO targetImageSizeUniform;
    private UniformDTO viewportSize;
    private UniformDTO viewportOrigin;
    private UniformDTO radiusDensityUniform;
    private final Vector3f xyDown = new Vector3f();
    private final Vector3f radiusDensityMode = new Vector3f();
    private final Vector2f viewportO = new Vector2f();
    private final Vector2f viewport = new Vector2f();
    private final Vector2f targetImageSize = new Vector2f();
    private TextureResourceRef targetTexture;

    @Override
    public void onInitialize() {
        targetImageSizeUniform = addUniformDeclaration("targetImageSize");
        xyDownUniform = addUniformDeclaration("xyDown");
        radiusDensityUniform = addUniformDeclaration("radiusDensityMode");
        viewportOrigin = addUniformDeclaration("viewportOrigin");
        viewportSize = addUniformDeclaration("viewportSize");
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.paintGizmoCompute;
    }

    @Override
    protected boolean isRenderable() {
        boolean isRenderable = editorRepository.gizmoType == GizmoType.PAINT && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
        if (isRenderable) {
            switch (editorRepository.paintingType) {
                case FOLIAGE: {
                    targetTexture = (TextureResourceRef) streamingService.stream(terrainRepository.instanceMaskMap, StreamableResourceType.TEXTURE);
                    break;
                }
                case TERRAIN: {
                    targetTexture = (TextureResourceRef) streamingService.stream(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE);
                    break;
                }
            }
        } else {
            targetTexture = null;
        }
        return isRenderable && targetTexture != null;
    }

    @Override
    protected void renderInternal() {
        FrameBufferObject fbo = fboRepository.postProcessingBuffer;
        fbo.bindForReadWrite();

        targetTexture.bindForWriting(1);

        radiusDensityMode.x = editorRepository.brushRadius;
        radiusDensityMode.y = editorRepository.brushDensity;
        radiusDensityMode.z = editorRepository.brushMode == BrushMode.ADD ? 1 : -1;

        xyDown.x = runtimeRepository.mouseX;
        xyDown.y = runtimeRepository.viewportH - runtimeRepository.mouseY;
        xyDown.z = runtimeRepository.mousePressed ? 1 : 0;

        viewport.x = runtimeRepository.viewportW;
        viewport.y = runtimeRepository.viewportH;

        viewportO.x = runtimeRepository.viewportX;
        viewportO.y = runtimeRepository.viewportY;

        targetImageSize.x = targetTexture.width * .5f; // DONT KNOW WHY THIS WORKS YET
        targetImageSize.y = targetTexture.height;

        shaderService.bindVec3(radiusDensityMode, radiusDensityUniform);
        shaderService.bindVec3(xyDown, xyDownUniform);
        shaderService.bindVec2(viewport, viewportSize);
        shaderService.bindVec2(targetImageSize, targetImageSizeUniform);
        shaderService.bindVec2(viewportO, viewportOrigin);

        shaderService.bindSampler2dDirect(fboRepository.gBufferDepthIndexSampler, 2);

        COMPUTE_RUNTIME_DATA.groupX = (fbo.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (fbo.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_BUFFER_UPDATE_BARRIER_BIT | GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        shaderService.dispatch(COMPUTE_RUNTIME_DATA);
    }

    @Override
    public String getTitle() {
        return "Paint gizmo";
    }
}
