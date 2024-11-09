package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.BrushMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.GizmoType;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.impl.AbstractQuadPassPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

public class PaintGizmoRenderingPass extends AbstractQuadPassPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO xyMouseUniform;
    private UniformDTO viewportSize;
    private UniformDTO viewportOrigin;
    private UniformDTO radiusDensityUniform;
    private UniformDTO hasSelection;
    private final Vector3f xyDown = new Vector3f();
    private final Vector3f radiusDensityMode = new Vector3f();
    private final Vector2f viewportO = new Vector2f();
    private final Vector2f viewport = new Vector2f();

    @Override
    public void onInitialize() {
        xyMouseUniform = addUniformDeclaration("xyMouse");
        radiusDensityUniform = addUniformDeclaration("radiusDensityMode");
        viewportOrigin = addUniformDeclaration("viewportOrigin");
        viewportSize = addUniformDeclaration("viewportSize");
        hasSelection = addUniformDeclaration("hasSelection");
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.paintGizmoRenderingShader;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.gBufferTarget;
    }

    @Override
    protected boolean isRenderable() {
        return runtimeRepository.isFocused && editorRepository.gizmoType == GizmoType.PAINT && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected void bindUniforms() {
        GL46.glEnable(GL11.GL_BLEND);

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

        shaderService.bindVec3(radiusDensityMode, radiusDensityUniform);
        shaderService.bindVec3(xyDown, xyMouseUniform);
        shaderService.bindVec2(viewport, viewportSize);
        shaderService.bindVec2(viewportO, viewportOrigin);

        shaderService.bindBoolean(checkIsValid(), hasSelection);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 0);
    }

    private boolean checkIsValid() {
        return switch (editorRepository.paintingType) {
            case FOLIAGE -> editorRepository.foliageForPainting != null && terrainRepository.instanceMaskMap != null;
            case TERRAIN -> terrainRepository.heightMapTexture != null;
            default -> false;
        };
    }

    @Override
    public String getTitle() {
        return "Paint gizmo rendering";
    }
}
