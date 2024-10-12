package com.pine.tasks;

import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.component.Transformation;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.rendering.LightService;
import com.pine.service.rendering.RenderingRequestService;
import com.pine.service.rendering.TransformationService;


/**
 * Collects all visible renderable elements into a list
 */
@PBean
public class RenderingTask extends AbstractTask implements Loggable {
    public static String MESH_COMP = MeshComponent.class.getSimpleName();

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
    public RenderingRequestService renderingRequestService;

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
        if (!entity.visible) {
            return;
        }
        Transformation t = entity.transformation;
        transformationService.updateMatrix(t);
        if (entity.components.containsKey(MESH_COMP)) {
            var meshComponent = (MeshComponent) entity.components.get(MESH_COMP);
            meshComponent.distanceFromCamera = transformationService.getDistanceFromCamera(t.translation);
            if (meshComponent.isInstancedRendering) {
                RenderingRequest request = renderingRequestService.prepareInstanced(meshComponent, t);
                if (request != null) {
                    request.renderIndex = renderIndex;
                    renderIndex += request.transformations.size() + 1;
                    renderingRepository.newRequests.add(request);
                }
            } else {
                RenderingRequest request = renderingRequestService.prepareNormal(meshComponent, t);
                if (request != null) {
                    request.renderIndex = renderIndex;
                    renderIndex++;
                    renderingRepository.newRequests.add(request);
                }
            }
        }

        for (var child : entity.transformation.children) {
            traverseTree(child.entity);
        }
    }

}
