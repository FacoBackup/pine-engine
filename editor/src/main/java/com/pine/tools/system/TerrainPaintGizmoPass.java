package com.pine.tools.system;

import com.pine.component.MeshComponent;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.BrushMode;
import com.pine.repository.EditorMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.ImageUtil;
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

public class TerrainPaintGizmoPass extends AbstractPaintGizmoPass {

    @Override
    public String getTitle() {
        return "Terrain painting";
    }

    @Override
    protected boolean isValidType() {
        return editorRepository.editorMode == EditorMode.FOLIAGE || editorRepository.editorMode == EditorMode.TERRAIN;
    }

    @Override
    protected TextureResourceRef updateTargetTexture() {
        switch (editorRepository.editorMode) {
            case FOLIAGE: {
                if (editorRepository.foliageForPainting != null) {
                    return (TextureResourceRef) streamingService.streamIn(terrainRepository.foliageMask, StreamableResourceType.TEXTURE);
                }
            }
            case TERRAIN: {
                return (TextureResourceRef) streamingService.streamIn(terrainRepository.heightMapTexture, StreamableResourceType.TEXTURE);
            }
        }
        return null;
    }
}
