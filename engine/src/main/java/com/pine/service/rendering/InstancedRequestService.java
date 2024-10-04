package com.pine.service.rendering;

import com.pine.component.CullingComponent;
import com.pine.component.InstancedMeshComponent;
import com.pine.component.Transformation;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.service.resource.ResourceService;
import com.pine.service.streaming.StreamingService;

import java.util.ArrayList;

@PBean
public class InstancedRequestService {
    public static RenderingMode DEFAULT_RENDERING_MODE = RenderingMode.TRIANGLES;

    @PInject
    public TransformationService transformationService;

    @PInject
    public ResourceService resourceService;

    @PInject
    public StreamingService streamingService;

    public RenderingRequest prepareInstanced(InstancedMeshComponent scene, Transformation t) {
        MeshStreamableResource mesh = scene.primitive;
        if (mesh == null || !mesh.isLoaded) {
            if (mesh != null) {
                streamingService.stream(scene.primitive);
            }
            return null;
        }

        prepareInstancedRequest(scene, t, mesh);
        fillInstanceRequest(scene, t);
        scene.renderRequest.mesh = mesh;
        return scene.renderRequest;
    }

    private void fillInstanceRequest(InstancedMeshComponent scene, Transformation t) {
        CullingComponent culling = (CullingComponent) scene.entity.components.get(CullingComponent.class.getSimpleName());
        for (var primitive : scene.primitives) {
            boolean culled = transformationService.isCulled(primitive.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions);
            if (culled) {
                continue;
            }
            transformationService.updateMatrix(primitive, t);
            transformationService.extractTransformations(primitive);
            scene.renderRequest.transformations.add(primitive);
        }
    }

    private static void prepareInstancedRequest(InstancedMeshComponent scene, Transformation t, MeshStreamableResource mesh) {
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
}
