package com.pine.tasks;

import com.pine.component.*;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.VoxelRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.environment.EnvironmentMapGenService;
import com.pine.service.rendering.LightService;
import com.pine.service.rendering.RenderingRequestService;
import com.pine.service.rendering.TransformationService;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;
import com.pine.service.streaming.ref.VoxelChunkResourceRef;

import java.util.Comparator;
import java.util.List;


/**
 * Collects all visible renderable elements into a list
 */
@PBean
public class RenderingTask extends AbstractTask implements Loggable {

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

    @PInject
    public VoxelRepository voxelRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public EnvironmentMapGenService environmentMapGenService;

    private int renderIndex = 0;

    @Override
    protected void tickInternal() {
        if (renderingRepository.infoUpdated) {
            return;
        }
        try {
            if (voxelRepository.grid != null) {
                int filledWithContent = 0;
                int filled = 0;

                // TODO - SORT BY DISTANCE
                for (var chunk : voxelRepository.grid.chunks) {
                    boolean culled = false;// transformationService.isCulled(chunk.getCenter(), 100, new Vector3f((float) chunk.getSize()));
                    if (culled || filled > 3) {
                        continue;
                    }
                    var chunkStream = (VoxelChunkResourceRef) streamingService.stream(chunk.getId(), StreamableResourceType.VOXEL_CHUNK);
                    renderingRepository.newVoxelChunks[filled] = chunkStream;
                    if (chunkStream != null) {
                        filledWithContent++;
                        chunkStream.size = chunk.getSize();
                        chunkStream.center = chunk.getCenter();
                        chunkStream.depth = chunk.getDepth();
                    }
                    filled++;
                }
                renderingRepository.voxelChunksFilled = filledWithContent;
            }

            var probes = worldRepository.components.get(ComponentType.ENVIRONMENT_PROBE);
            if(environmentMapGenService.isBaked) {
                var closest3 = findClosestPoints(probes, 3);
                for (int i = 0; i < 3; i++) {
                    renderingRepository.environmentMaps[i] = i >= closest3.size() ? null : (EnvironmentMapResourceRef) streamingService.stream(closest3.get(i).entity.id(), StreamableResourceType.ENVIRONMENT_MAP);
                }
            }

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

    private List<AbstractComponent> findClosestPoints(List<AbstractComponent> points, int k) {
        points.sort(Comparator.comparingDouble(t -> transformationService.getDistanceFromCamera(t.entity.transformation.translation)));
        return points.subList(0, Math.min(k, points.size()));
    }

    private void traverseTree(Entity entity) {
        if (!entity.visible) {
            return;
        }
        Transformation t = entity.transformation;
        transformationService.updateMatrix(t);
        if (entity.components.containsKey(ComponentType.MESH)) {
            var meshComponent = (MeshComponent) entity.components.get(ComponentType.MESH);
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
