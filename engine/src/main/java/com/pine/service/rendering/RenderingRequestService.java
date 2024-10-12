package com.pine.service.rendering;

import com.pine.component.MeshComponent;
import com.pine.component.Transformation;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.MeshStreamableResource;
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
        MeshStreamableResource mesh = selectLOD(scene);
        if (mesh == null) return null;

        prepareTransformations(scene, t, mesh);
        fillInstanceRequest(scene, t);
        scene.renderRequest.mesh = mesh;
        return scene.renderRequest;
    }

    private void fillInstanceRequest(MeshComponent scene, Transformation t) {
        for (var primitive : scene.primitives) {
            transformationService.updateMatrix(primitive, t);
            primitive.isCulled = scene.isCullingEnabled && transformationService.isCulled(primitive.translation, scene.maxDistanceFromCamera, scene.boundingBoxSize);
            if (t.isCulled) {
                continue;
            }
            transformationService.extractTransformations(primitive);
            scene.renderRequest.transformations.add(primitive);
        }
        prepareMaterial(scene, scene.renderRequest);
    }

    private static void prepareTransformations(MeshComponent scene, Transformation t, MeshStreamableResource mesh) {
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
        MeshStreamableResource mesh = selectLOD(scene);
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
        if (scene.albedo != null) {
            if (scene.albedo.isLoaded()) {
                renderRequest.albedo = scene.albedo.texture;
            } else {
                streamingService.stream(scene.albedo);
            }
        }
        if (scene.roughness != null) {
            if (scene.roughness.isLoaded()) {
                renderRequest.roughness = scene.roughness.texture;
            } else {
                streamingService.stream(scene.roughness);
            }
        }
        if (scene.metallic != null) {
            if (scene.metallic.isLoaded()) {
                renderRequest.metallic = scene.metallic.texture;
            } else {
                streamingService.stream(scene.metallic);
            }
        }
        if (scene.ao != null) {
            if (scene.ao.isLoaded()) {
                renderRequest.ao = scene.ao.texture;
            } else {
                streamingService.stream(scene.ao);
            }
        }
        if (scene.normal != null) {
            if (scene.normal.isLoaded()) {
                renderRequest.normal = scene.normal.texture;
            } else {
                streamingService.stream(scene.normal);
            }
        }
        if (scene.heightMap != null) {
            if (scene.heightMap.isLoaded()) {
                renderRequest.heightMap = scene.heightMap.texture;
            } else {
                streamingService.stream(scene.heightMap);
            }
        }
        if (scene.materialMask != null) {
            if (scene.materialMask.isLoaded()) {
                renderRequest.materialMask = scene.materialMask.texture;
            } else {
                streamingService.stream(scene.materialMask);
            }
        }
        renderRequest.parallaxHeightScale = scene.parallaxHeightScale;
        renderRequest.parallaxLayers = scene.parallaxLayers;
        renderRequest.useParallax = scene.useParallax;
    }

    protected @Nullable MeshStreamableResource selectLOD(MeshComponent scene) {
        MeshStreamableResource finalResource = null;
        boolean isLOD0Loaded = scene.lod0 != null && scene.lod0.isLoaded();
        boolean isLOD1Loaded = scene.lod1 != null && scene.lod1.isLoaded();
        boolean isLOD2Loaded = scene.lod2 != null && scene.lod2.isLoaded();
        boolean isLOD3Loaded = scene.lod3 != null && scene.lod3.isLoaded();

        if (scene.lod0 != null && scene.distanceFromCamera <= scene.lod0DistanceUntil) {
            if (isLOD0Loaded) {
                finalResource = scene.lod0;
            } else {
                streamingService.stream(scene.lod0);
            }
        }

        if (scene.lod1 != null && scene.distanceFromCamera <= scene.lod1DistanceUntil && scene.distanceFromCamera > scene.lod0DistanceUntil) {
            if (isLOD1Loaded) {
                finalResource = scene.lod1;
            } else {
                streamingService.stream(scene.lod1);
            }
        }

        if (scene.lod2 != null && scene.distanceFromCamera <= scene.lod2DistanceUntil && scene.distanceFromCamera > scene.lod1DistanceUntil) {
            if (isLOD2Loaded) {
                finalResource = scene.lod2;
            } else {
                streamingService.stream(scene.lod2);
            }
        }

        if (scene.lod3 != null && scene.distanceFromCamera <= scene.lod3DistanceUntil && scene.distanceFromCamera > scene.lod2DistanceUntil) {
            if (isLOD3Loaded) {
                finalResource = scene.lod3;
            } else {
                streamingService.stream(scene.lod3);
            }
        }

        if (finalResource == null && scene.lod4 != null) {
            if (scene.lod4.isLoaded()) {
                finalResource = scene.lod4;
            } else {
                streamingService.stream(scene.lod4);
            }
        }

        if (finalResource == null) {
            if (isLOD3Loaded) {
                finalResource = scene.lod3;
            }
            if (isLOD2Loaded) {
                finalResource = scene.lod2;
            }
            if (isLOD1Loaded) {
                finalResource = scene.lod1;
            }
            if (isLOD0Loaded) {
                finalResource = scene.lod0;
            }
        }

        return finalResource;
    }
}
