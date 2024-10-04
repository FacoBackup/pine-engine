package com.pine.tasks;

import com.pine.Loggable;
import com.pine.component.*;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.service.rendering.InstancedRequestService;
import com.pine.service.rendering.LightService;
import com.pine.service.rendering.TransformationService;
import com.pine.service.streaming.StreamingService;


/**
 * Collects all visible renderable elements into a list
 */
@PBean
public class RenderingTask extends AbstractTask implements Loggable {

    public static String INSTANCED_COMP = InstancedMeshComponent.class.getSimpleName();
    public static String PRIMITIVE_COMP = MeshComponent.class.getSimpleName();

    @PInject
    public StreamingService streamingService;

    @PInject
    public CameraRepository camera;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public LightService lightService;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public TransformationService transformationService;

    @PInject
    public InstancedRequestService instancedRequestService;

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
            renderingRepository.newRequests.clear();

            traverseTree(worldRepository.rootEntity);
            lightService.packageLights();

            renderingRepository.infoUpdated = true;
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    private void traverseTree(Entity entity) {
        Transformation t = entity.transformation;
        transformationService.updateMatrix(t);
        if (entity.components.containsKey(INSTANCED_COMP)) {
            RenderingRequest request = instancedRequestService.prepareInstanced((InstancedMeshComponent) entity.components.get(INSTANCED_COMP), t);
            if (request != null) {
                request.renderIndex = renderIndex;
                renderIndex += request.transformations.size() + 1;
                renderingRepository.newRequests.add(request);
            }
        }

        if (entity.components.containsKey(PRIMITIVE_COMP)) {
            RenderingRequest request = preparePrimitive((MeshComponent) entity.components.get(PRIMITIVE_COMP), t);
            if (request != null) {
                request.renderIndex = renderIndex;
                renderIndex++;
                renderingRepository.newRequests.add(request);
            }
        }

        for (var child : entity.transformation.children) {
            traverseTree(child.entity);
        }
    }

    private RenderingRequest preparePrimitive(MeshComponent scene, Transformation transform) {
        MeshStreamableResource mesh = scene.primitive;
        if (mesh == null || !mesh.isLoaded) {
            if (mesh != null) {
                streamingService.stream(scene.primitive);
            }
            return null;
        }
        var culling = (CullingComponent) scene.entity.components.get(CullingComponent.class.getSimpleName());
        if (!transformationService.isCulled(transform.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions)) {
            if (transform.renderRequest == null) {
                transform.renderRequest = new RenderingRequest(mesh, scene.entity.transformation);
            }
            transform.renderRequest.mesh = mesh;
            transformationService.extractTransformations(transform);
            return transform.renderRequest;
        }
        return null;
    }
}
