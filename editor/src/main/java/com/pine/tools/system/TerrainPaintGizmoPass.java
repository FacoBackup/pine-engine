package com.pine.tools.system;

import com.pine.repository.EditorMode;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.ref.TextureResourceRef;

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
