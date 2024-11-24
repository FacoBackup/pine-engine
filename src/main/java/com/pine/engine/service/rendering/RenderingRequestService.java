package com.pine.engine.service.rendering;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.engine.component.CullingComponent;
import com.pine.engine.component.MeshComponent;
import com.pine.engine.component.TransformationComponent;
import com.pine.engine.repository.EngineRepository;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.repository.rendering.RenderingRepository;
import com.pine.engine.repository.rendering.RenderingRequest;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.streaming.StreamingService;
import com.pine.engine.service.streaming.ref.MaterialResourceRef;
import com.pine.engine.service.streaming.ref.MeshResourceRef;
import org.jetbrains.annotations.Nullable;

@PBean
public class RenderingRequestService {
    @PInject
    public TransformationService transformationService;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public EngineRepository engineRepository;

    public void updateCullingStatus(CullingComponent component, TransformationComponent transform) {
        if (component != null) {
            if (component.isCullingEnabled && !engineRepository.disableCullingGlobally && transformationService.isCulled(transform, component)) {
                worldRepository.culled.put(component.getEntityId(), true);
            } else {
                worldRepository.culled.remove(component.getEntityId());
            }
        }
    }

    public void prepareMesh(MeshComponent scene, CullingComponent culling, TransformationComponent transform) {
        if (scene != null && !worldRepository.culled.containsKey(scene.getEntityId())) {
            if (scene.renderRequest == null) {
                scene.renderRequest = new RenderingRequest();
            }
            MeshResourceRef mesh = selectLOD(scene, culling);
            if (mesh == null) return;

            scene.renderRequest.mesh = mesh;
            scene.renderRequest.entity = scene.getEntityId();
            prepareMaterial(scene, scene.renderRequest);

            scene.renderRequest.mesh = mesh;
            scene.renderRequest.modelMatrix = transform.modelMatrix;
        }
    }

    private void prepareMaterial(MeshComponent scene, RenderingRequest renderRequest) {
        renderRequest.material = (MaterialResourceRef) streamingService.streamIn(scene.material, StreamableResourceType.MATERIAL);
    }

    protected @Nullable MeshResourceRef selectLOD(MeshComponent scene, CullingComponent culling) {
        MeshResourceRef finalResource = null;

        if (scene.lod0 != null && culling.distanceFromCamera <= scene.lod0DistanceUntil) {
            finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod0, StreamableResourceType.MESH);
        }

        if (scene.lod1 != null && culling.distanceFromCamera <= scene.lod1DistanceUntil && culling.distanceFromCamera > scene.lod0DistanceUntil) {
            finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod1, StreamableResourceType.MESH);
        }

        if (scene.lod2 != null && culling.distanceFromCamera <= scene.lod2DistanceUntil && culling.distanceFromCamera > scene.lod1DistanceUntil) {
            finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod2, StreamableResourceType.MESH);
        }

        if (scene.lod3 != null && culling.distanceFromCamera <= scene.lod3DistanceUntil && culling.distanceFromCamera > scene.lod2DistanceUntil) {
            finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod3, StreamableResourceType.MESH);
        }

        if (finalResource == null && scene.lod4 != null) {
            finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod4, StreamableResourceType.MESH);
        }

        if (finalResource == null) {
            if (scene.lod3 != null) {
                finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod3, StreamableResourceType.MESH);
            }
            if (scene.lod2 != null && finalResource == null) {
                finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod2, StreamableResourceType.MESH);
            }
            if (scene.lod1 != null && finalResource == null) {
                finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod1, StreamableResourceType.MESH);
            }
            if (scene.lod0 != null && finalResource == null) {
                finalResource = (MeshResourceRef) streamingService.streamIn(scene.lod0, StreamableResourceType.MESH);
            }
        }


        return finalResource;
    }
}
