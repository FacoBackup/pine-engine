package com.pine.service.rendering;

import com.pine.component.MeshComponent;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EngineSettingsRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;
import org.jetbrains.annotations.Nullable;

@PBean
public class RenderingRequestService {
    @PInject
    public TransformationService transformationService;

    @PInject
    public StreamingService streamingService;

    @PInject
    public EngineSettingsRepository engineSettings;

    @PInject
    public RenderingRepository renderingRepository;

    public RenderingRequest prepare(MeshComponent scene, TransformationComponent transform) {
        MeshResourceRef mesh = selectLOD(scene);
        if (mesh == null) return null;

        if (scene.renderRequest == null) {
            scene.renderRequest = new RenderingRequest();
        }

        if (scene.isCullingEnabled) {
            scene.renderRequest.isCulled = transformationService.isCulled(transform.translation, scene.maxDistanceFromCamera, scene.cullingSphereRadius);
        } else {
            scene.renderRequest.isCulled = false;
        }

        if (!scene.renderRequest.isCulled) {
            scene.renderRequest.mesh = mesh;
            scene.renderRequest.entity = scene.getEntityId();
            prepareMaterial(scene, scene.renderRequest);

            scene.renderRequest.mesh = mesh;
            scene.renderRequest.modelMatrix = transform.modelMatrix;
            return scene.renderRequest;
        }
        return null;
    }

    private void prepareMaterial(MeshComponent scene, RenderingRequest renderRequest) {
        renderRequest.material = (MaterialResourceRef) streamingService.stream(scene.material, StreamableResourceType.MATERIAL);
    }

    protected @Nullable MeshResourceRef selectLOD(MeshComponent scene) {
        MeshResourceRef finalResource = null;

        if (scene.lod0 != null && scene.distanceFromCamera <= scene.lod0DistanceUntil) {
            finalResource = (MeshResourceRef) streamingService.stream(scene.lod0, StreamableResourceType.MESH);
        }

        if (scene.lod1 != null && scene.distanceFromCamera <= scene.lod1DistanceUntil && scene.distanceFromCamera > scene.lod0DistanceUntil) {
            finalResource = (MeshResourceRef) streamingService.stream(scene.lod1, StreamableResourceType.MESH);
        }

        if (scene.lod2 != null && scene.distanceFromCamera <= scene.lod2DistanceUntil && scene.distanceFromCamera > scene.lod1DistanceUntil) {
            finalResource = (MeshResourceRef) streamingService.stream(scene.lod2, StreamableResourceType.MESH);
        }

        if (scene.lod3 != null && scene.distanceFromCamera <= scene.lod3DistanceUntil && scene.distanceFromCamera > scene.lod2DistanceUntil) {
            finalResource = (MeshResourceRef) streamingService.stream(scene.lod3, StreamableResourceType.MESH);
        }

        if (finalResource == null && scene.lod4 != null) {
            finalResource = (MeshResourceRef) streamingService.stream(scene.lod4, StreamableResourceType.MESH);
        }

        if (finalResource == null) {
            if (scene.lod3 != null) {
                finalResource = (MeshResourceRef) streamingService.stream(scene.lod3, StreamableResourceType.MESH);
            }
            if (scene.lod2 != null && finalResource == null) {
                finalResource = (MeshResourceRef) streamingService.stream(scene.lod2, StreamableResourceType.MESH);
            }
            if (scene.lod1 != null && finalResource == null) {
                finalResource = (MeshResourceRef) streamingService.stream(scene.lod1, StreamableResourceType.MESH);
            }
            if (scene.lod0 != null && finalResource == null) {
                finalResource = (MeshResourceRef) streamingService.stream(scene.lod0, StreamableResourceType.MESH);
            }
        }


        return finalResource;
    }
}
