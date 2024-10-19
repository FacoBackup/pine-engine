package com.pine.service.rendering;

import com.pine.component.MeshComponent;
import com.pine.component.Transformation;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.streaming.StreamingService;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@PBean
public class RenderingRequestService {
    @PInject
    public TransformationService transformationService;

    @PInject
    public StreamingService streamingService;

    public RenderingRequest prepareInstanced(MeshComponent scene, Transformation t) {
        MeshResourceRef mesh = selectLOD(scene);
        if (mesh == null) return null;

        prepareTransformations(scene, t, mesh);
        fillInstanceRequest(scene, t);
        scene.renderRequest.mesh = mesh;
        if (scene.renderRequest.transformations.isEmpty()) {
            return null;
        }
        return scene.renderRequest;
    }

    private void fillInstanceRequest(MeshComponent scene, Transformation t) {
        for (var primitive : scene.primitives) {
            transformationService.updateMatrix(primitive, t);
            primitive.isCulled = scene.isCullingEnabled && transformationService.isCulled(primitive.translation, scene.maxDistanceFromCamera, scene.boundingBoxSize);
            if (primitive.isCulled) {
                continue;
            }
            transformationService.extractTransformations(primitive);
            scene.renderRequest.transformations.add(primitive);
        }
        prepareMaterial(scene, scene.renderRequest);
    }

    private static void prepareTransformations(MeshComponent scene, Transformation t, MeshResourceRef mesh) {
        if (scene.primitives.size() > scene.numberOfInstances) {
            scene.primitives = new ArrayList<>(scene.primitives.subList(0, scene.numberOfInstances));
        } else if (scene.primitives.size() < scene.numberOfInstances) {
            for (int i = scene.primitives.size(); i < scene.numberOfInstances; i++) {
                scene.primitives.add(new Transformation(scene.entity, true));
            }
        }

        if (scene.renderRequest == null) {
            scene.renderRequest = new RenderingRequest(mesh, t, new ArrayList<>());
        }
        scene.renderRequest.transformations.clear();
    }


    public RenderingRequest prepareNormal(MeshComponent scene, Transformation transform) {
        MeshResourceRef mesh = selectLOD(scene);
        if (mesh == null) return null;

        transform.isCulled = scene.isCullingEnabled && transformationService.isCulled(transform.translation, scene.maxDistanceFromCamera, scene.boundingBoxSize);
        if (!transform.isCulled) {
            if (transform.renderRequest == null) {
                transform.renderRequest = new RenderingRequest(mesh, transform);
            }
            prepareMaterial(scene, transform.renderRequest);

            transform.renderRequest.mesh = mesh;
            if (!transform.renderRequest.transformations.isEmpty()) {
                transform.renderRequest.transformations.clear();
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

        return finalResource;
    }
}
