package com.pine.tasks;

import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.*;
import com.pine.repository.CameraRepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.InstancedRequestService;
import com.pine.service.LightService;
import com.pine.service.TransformationService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.primitives.mesh.Mesh;


/**
 * Collects all visible renderable elements into a list
 */
@PBean
public class RenderingTask extends AbstractTask implements Loggable {

    public static String INSTANCED_COMP = InstancedPrimitiveComponent.class.getSimpleName();
    public static String PRIMITIVE_COMP = PrimitiveComponent.class.getSimpleName();
    private static final MeshRuntimeData DEFAULT_RENDER_REQUEST = new MeshRuntimeData(MeshRenderingMode.TRIANGLES);

    @PInject
    public ResourceService resourceService;

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
            PrimitiveRenderRequest request = instancedRequestService.prepareInstanced((InstancedPrimitiveComponent) entity.components.get(INSTANCED_COMP), t);
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

        for (var child : entity.transformation.children) {
            traverseTree(child.entity);
        }
    }

    private PrimitiveRenderRequest preparePrimitive(PrimitiveComponent scene, Transformation transform) {
        if (scene.primitive == null) {
            return null;
        }
        var mesh = scene.primitive.resource = scene.primitive.resource == null ? (Mesh) resourceService.getOrCreateResource(scene.primitive.id) : scene.primitive.resource;
        if (mesh != null) {
            var culling = (CullingComponent) scene.entity.components.get(CullingComponent.class.getSimpleName());
            if (!transformationService.isCulled(transform.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions)) {
                if (transform.renderRequest == null) {
                    transform.renderRequest = new PrimitiveRenderRequest(mesh, DEFAULT_RENDER_REQUEST, scene.entity.transformation);
                }
                transform.renderRequest.mesh = mesh;
                transformationService.extractTransformations(transform);
                return transform.renderRequest;
            }
        }
        return null;
    }
}
