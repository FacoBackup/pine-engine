package com.pine.service;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.CullingComponent;
import com.pine.component.InstancedPrimitiveComponent;
import com.pine.component.Transformation;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.primitives.mesh.Primitive;

import java.util.ArrayList;

@PBean
public class InstancedRequestService {
    public static MeshRenderingMode DEFAULT_RENDERING_MODE = MeshRenderingMode.TRIANGLES;

    @PInject
    public TransformationService transformationService;

    @PInject
    public ResourceService resourceService;

    public PrimitiveRenderRequest prepareInstanced(InstancedPrimitiveComponent scene, Transformation t) {
        if (scene.primitive == null) {
            return null;
        }

        var mesh = scene.primitive.resource = scene.primitive.resource == null ? (Primitive) resourceService.getOrCreateResource(scene.primitive.id) : scene.primitive.resource;
        if (mesh == null) {
            return null;
        }

        prepareInstancedRequest(scene, t, mesh);
        fillInstanceRequest(scene, t);
        scene.renderRequest.primitive = mesh;
        return scene.renderRequest;
    }

    private void fillInstanceRequest(InstancedPrimitiveComponent scene, Transformation t) {
        int realNumberOfInstances = 0;
        CullingComponent culling = (CullingComponent) scene.entity.components.get(CullingComponent.class.getSimpleName());
        for (var primitive : scene.primitives) {
            boolean culled = transformationService.isCulled(primitive.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions);
            if (culled) {
                continue;
            }
            realNumberOfInstances++;

            transformationService.updateMatrix(primitive, t);
            transformationService.extractTransformations(primitive);
            scene.renderRequest.transformations.add(primitive);
        }
        scene.runtimeData.instanceCount = realNumberOfInstances;
    }

    private static void prepareInstancedRequest(InstancedPrimitiveComponent scene, Transformation t, Primitive mesh) {
        scene.runtimeData = scene.runtimeData == null ? new MeshRuntimeData(DEFAULT_RENDERING_MODE) : scene.runtimeData;
        if (scene.primitives.size() > scene.numberOfInstances) {
            scene.primitives = new ArrayList<>(scene.primitives.subList(0, scene.numberOfInstances));
        } else if (scene.primitives.size() < scene.numberOfInstances) {
            for (int i = scene.primitives.size(); i < scene.numberOfInstances; i++) {
                scene.primitives.add(new Transformation(scene.entity, true));
            }
        }

        if (scene.renderRequest == null) {
            scene.renderRequest = new PrimitiveRenderRequest(mesh, scene.runtimeData, t, new ArrayList<>());
        }
        scene.renderRequest.transformations.clear();
    }
}
