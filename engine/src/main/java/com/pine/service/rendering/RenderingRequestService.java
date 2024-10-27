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

import java.util.ArrayList;

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

    public RenderingRequest prepareInstanced(MeshComponent scene, TransformationComponent t) {
        MeshResourceRef mesh = selectLOD(scene);
        if (mesh == null) return null;

        prepareTransformations(scene, t, mesh);
        fillInstanceRequest(scene, t);
        scene.renderRequest.mesh = mesh;
        if (scene.renderRequest.transformationComponents.isEmpty()) {
            return null;
        }
        return scene.renderRequest;
    }

    private void fillInstanceRequest(MeshComponent scene, TransformationComponent t) {
        for (var primitive : scene.instances) {
            transformationService.transform(primitive, t);
            if (scene.isCullingEnabled && !engineSettings.disableCullingGlobally) {
                primitive.isCulled = transformationService.isCulled(primitive.translation, scene.maxDistanceFromCamera, scene.boundingBoxSize);
            } else {
                primitive.isCulled = false;
            }
            if (primitive.isCulled) {
                continue;
            }
            transformationService.extractTransformations(primitive);
            scene.renderRequest.transformationComponents.add(primitive);
        }
        prepareMaterial(scene, scene.renderRequest);
    }

    private void prepareTransformations(MeshComponent scene, TransformationComponent t, MeshResourceRef mesh) {
        if (scene.instances.size() > scene.numberOfInstances) {
            scene.instances = new ArrayList<>(scene.instances.subList(0, scene.numberOfInstances));
        } else if (scene.instances.size() < scene.numberOfInstances) {
            for (int i = scene.instances.size(); i < scene.numberOfInstances; i++) {
                scene.instances.add(new TransformationComponent(scene.entity));
            }
        }

        if (scene.renderRequest == null) {
            scene.renderRequest = new RenderingRequest(mesh, t, new ArrayList<>());
        }
        renderingRepository.newToBeRendered.put(scene.entity.id(), scene.renderRequest);
        scene.renderRequest.entity = scene.entity;
        scene.renderRequest.transformationComponents.clear();
    }


    public RenderingRequest prepareNormal(MeshComponent scene, TransformationComponent transform) {
        MeshResourceRef mesh = selectLOD(scene);
        if (mesh == null) return null;

        if (scene.isCullingEnabled && !engineSettings.disableCullingGlobally) {
            transform.isCulled = transformationService.isCulled(transform.translation, scene.maxDistanceFromCamera, scene.boundingBoxSize);
        } else {
            transform.isCulled = false;
        }
        if (!transform.isCulled) {
            if (transform.renderRequest == null) {
                transform.renderRequest = new RenderingRequest(mesh, transform);
            }
            renderingRepository.newToBeRendered.put(scene.entity.id(), transform.renderRequest);
            transform.renderRequest.entity = scene.entity;
            prepareMaterial(scene, transform.renderRequest);

            transform.renderRequest.mesh = mesh;
            if (!transform.renderRequest.transformationComponents.isEmpty()) {
                transform.renderRequest.transformationComponents.clear();
            }
            transformationService.extractTransformations(transform);
            return transform.renderRequest;
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
