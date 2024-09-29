package com.pine.tasks;

import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.*;
import com.pine.repository.CameraRepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.LightService;
import com.pine.service.TransformationService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.primitives.mesh.Primitive;

import java.util.ArrayList;
import java.util.List;


/**
 * Collects all visible renderable elements into a list
 */
@PBean
public class RenderingTask extends AbstractTask implements Loggable {

    public static String TRANSFORMATION_COMP = TransformationComponent.class.getSimpleName();
    public static String INSTANCED_COMP = InstancedPrimitiveComponent.class.getSimpleName();
    public static String PRIMITIVE_COMP = PrimitiveComponent.class.getSimpleName();
    public static MeshRenderingMode DEFAULT_RENDERING_MODE = MeshRenderingMode.TRIANGLES;
    private static final MeshRuntimeData DEFAULT_RENDER_REQUEST = new MeshRuntimeData(MeshRenderingMode.TRIANGLES);

    @PInject
    public PrimitiveComponent scenes;

    @PInject
    public InstancedPrimitiveComponent instancedComponents;

    @PInject
    public ResourceService resourceService;

    @PInject
    public CameraRepository camera;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public TransformationComponent transformationComponent;

    @PInject
    public LightService lightService;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public TransformationService transformationService;

    private int renderIndex = 0;

    @Override
    protected void tickInternal() {
        if (renderingRepository.infoUpdated) {
            return;
        }
        try {
            renderIndex = 0;
            renderingRepository.offset = 0;
            renderingRepository.auxAddedToBufferEntities.clear();
            renderingRepository.pendingTransformationsInternal = 0;
            DEFAULT_RENDER_REQUEST.mode = DEFAULT_RENDERING_MODE;
            renderingRepository.newRequests.clear();

            traverseTree(worldRepository.rootEntity);
            lightService.packageLights();

            renderingRepository.infoUpdated = true;
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    private void traverseTree(Entity entity) {
        TransformationComponent t = null;
        if (entity.components.containsKey(TRANSFORMATION_COMP)) {
            transformationService.updateMatrix(t = (TransformationComponent) entity.components.get(TRANSFORMATION_COMP));
        }

        if (entity.components.containsKey(INSTANCED_COMP)) {
            PrimitiveRenderRequest request = prepareInstanced((InstancedPrimitiveComponent) entity.components.get(INSTANCED_COMP), t);
            if (request != null) {
                request.renderIndex = renderIndex;
                renderIndex += request.transformations.size() + 1;
                renderingRepository.newRequests.add(request);
            }
        }

        if (entity.components.containsKey(PRIMITIVE_COMP)) {
            PrimitiveRenderRequest request = preparePrimitive((PrimitiveComponent) entity.components.get(PRIMITIVE_COMP), t);
            if (request != null) {
                request.renderIndex = renderIndex;
                renderIndex++;
                renderingRepository.newRequests.add(request);
            }
        }

        for (var child : entity.children) {
            traverseTree(child);
        }
    }

    private PrimitiveRenderRequest preparePrimitive(PrimitiveComponent scene, TransformationComponent transform) {
        if (scene.primitive == null) {
            return null;
        }
        var mesh = scene.primitive.resource = scene.primitive.resource == null ? (Primitive) resourceService.getOrCreateResource(scene.primitive.id) : scene.primitive.resource;
        if (mesh != null) {
            var culling = (CullingComponent) scene.entity.components.get(CullingComponent.class.getSimpleName());
            if (!transformationService.isCulled(transform.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions)) {
                if (transform.renderRequest == null) {
                    transform.renderRequest = new PrimitiveRenderRequest(mesh, DEFAULT_RENDER_REQUEST, (TransformationComponent) scene.entity.components.get(TRANSFORMATION_COMP));
                }
                transform.renderRequest.primitive = mesh;
                transformationService.extractTransformations(transform);
                return transform.renderRequest;
            }
        }
        return null;
    }

    private PrimitiveRenderRequest prepareInstanced(InstancedPrimitiveComponent scene, TransformationComponent t) {
        if (scene.primitive == null) {
            return null;
        }

        var mesh = scene.primitive.resource = scene.primitive.resource == null ? (Primitive) resourceService.getOrCreateResource(scene.primitive.id) : scene.primitive.resource;
        if (mesh == null) {
            return null;
        }

        scene.runtimeData = scene.runtimeData == null ? new MeshRuntimeData(DEFAULT_RENDERING_MODE) : scene.runtimeData;
        if (scene.primitives.size() > scene.numberOfInstances) {
            scene.primitives = new ArrayList<>(scene.primitives.subList(0, scene.numberOfInstances));
        } else if (scene.primitives.size() < scene.numberOfInstances) {
            for (int i = scene.primitives.size(); i < scene.numberOfInstances; i++) {
                scene.primitives.add(new TransformationComponent(scene.entity, transformationComponent.bag));
            }
        }

        if (scene.renderRequest == null) {
            scene.renderRequest = new PrimitiveRenderRequest(mesh, scene.runtimeData, t, new ArrayList<>());
        }
        scene.renderRequest.transformations.clear();

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

        scene.renderRequest.primitive = mesh;
        return scene.renderRequest;
    }


}
