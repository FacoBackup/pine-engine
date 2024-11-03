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
import com.pine.service.system.impl.AbstractQuadPassPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class PaintGizmoRenderingPass extends AbstractQuadPassPass {
    private static final Vector3f EMPTY = new Vector3f(-1);
    private static final int LOCAL_SIZE_X = 4;
    private static final int LOCAL_SIZE_Y = 4;

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
        return fboRepository.auxBuffer;
    }

    @Override
    protected boolean isRenderable() {
        return editorRepository.gizmoType == GizmoType.PAINT && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
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
        shaderService.bindBoolean(editorRepository.foliageForPainting != null, hasSelection);
        shaderService.bindSampler2dDirect(fboRepository.gBufferDepthIndexSampler, 0);
    }

    @Override
    public String getTitle() {
        return "Paint gizmo rendering";
    }
}
