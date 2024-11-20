package com.pine.tools.system;

import com.pine.component.MeshComponent;
import com.pine.repository.EditorMode;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.ImageUtil;
import com.pine.service.streaming.ref.TextureResourceRef;

public class MaterialPaintGizmoPass extends AbstractPaintGizmoPass {

    @Override
    public String getTitle() {
        return "Material painting";
    }

    @Override
    protected boolean isValidType() {
        return editorRepository.editorMode == EditorMode.MATERIAL;
    }

    @Override
    protected TextureResourceRef updateTargetTexture() {
        if (editorRepository.mainSelection != null) {
            MeshComponent meshComponent = world.bagMeshComponent.get(editorRepository.mainSelection);
            if (worldService.isMeshReady(meshComponent)) {
                if (meshComponent.writtenMaterialTextureSize != meshComponent.materialTextureSize) {
                    ImageUtil.generateTexture(meshComponent.materialTextureSize, meshComponent.materialTextureSize, importerService.getPathToFile(meshComponent.getEntityId(), StreamableResourceType.TEXTURE));
                    meshComponent.writtenMaterialTextureSize = meshComponent.materialTextureSize;
                    streamingService.repository.discardedResources.remove(meshComponent.getEntityId());
                }
                return (TextureResourceRef) streamingService.streamIn(editorRepository.mainSelection, StreamableResourceType.TEXTURE);
            }
        }
        return null;
    }
}
